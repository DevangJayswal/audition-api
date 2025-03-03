package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.Comment;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import com.github.tomakehurst.wiremock.http.Fault;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureWireMock(port = 0) // Use random port for wiremock server
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
class AuditionIntegrationClientTest {

    @Autowired
    private AuditionIntegrationClient auditionIntegrationClient;

    @BeforeEach
    public void setup() {
        // Reset WireMock before each test
        WireMock.reset();
    }

    @Test
    void getPosts() {
        stubFor(get(urlEqualTo("/posts/999"))
            .willReturn(aResponse()
                .withStatus(404)));

        assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById("999"));

    }

    @Test
    void getPostById_ServerError() {
        stubFor(get(urlEqualTo("/posts/500"))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("Internal Server Error")));

        assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById("500"));
    }

    @Test
    void getPostById_OtherClientError() {
        stubFor(get(urlEqualTo("/posts/400"))
            .willReturn(aResponse()
                .withStatus(400)
                .withBody("Bad Request")));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostById("400"));

        assertEquals("External API Error", exception.getTitle());
        assertEquals(400, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Error occurred while fetching post with id 400"));
    }

    @Test
    void getPostById_UnexpectedError() {
        stubFor(get(urlEqualTo("/posts/error"))
            .willReturn(aResponse()
                .withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById("error"));
    }

    @Test
    void getComments_Non2xxStatus() {
        stubFor(get(urlEqualTo("/comments?postId=1"))
            .willReturn(aResponse()
                .withStatus(300)));

        Map<String, String> params = Collections.singletonMap("postId", "1");
        List<Comment> comments = auditionIntegrationClient.getComments(params);
        assertTrue(comments.isEmpty());
    }

    @Test
    void getComments_NotFound() {
        stubFor(get(urlEqualTo("/comments?postId=999"))
            .willReturn(aResponse()
                .withStatus(404)));

        Map<String, String> params = Collections.singletonMap("postId", "999");
        List<Comment> comments = auditionIntegrationClient.getComments(params);
        assertTrue(comments.isEmpty());
    }

    @Test
    void getComments_OtherClientError() {
        stubFor(get(urlEqualTo("/comments?postId=400"))
            .willReturn(aResponse()
                .withStatus(400)
                .withBody("Bad Request")));

        Map<String, String> params = Collections.singletonMap("postId", "400");
        List<Comment> comments = auditionIntegrationClient.getComments(params);
        assertTrue(comments.isEmpty());
    }

    @Test
    void getComments_UnexpectedError() {
        stubFor(get(urlEqualTo("/comments?postId=error"))
            .willReturn(aResponse()
                .withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        Map<String, String> params = Collections.singletonMap("postId", "error");
        List<Comment> comments = auditionIntegrationClient.getComments(params);
        assertTrue(comments.isEmpty());
    }
}