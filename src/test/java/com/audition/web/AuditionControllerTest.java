package com.audition.web;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.audition.configuration.SecurityConfigTest;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.StreamUtils;


@SpringBootTest
@AutoConfigureWireMock(port = 0) // Use random port for wiremock server
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@Import(SecurityConfigTest.class) // disable security for testing
@ActiveProfiles("test")
class AuditionControllerTest {

    @Autowired
    MockMvc mockMvc;

    String mockPostsUrl = "/posts";

    @BeforeEach
    public void setup() {
        // Reset WireMock before each test
        WireMock.reset();
    }

    @Test
    void getPosts() throws Exception {

        // Read the mock response content from the file into a String
        ClassPathResource resource = new ClassPathResource("mocks/posts.json");
        String postsMockJsonResponse = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        // Stubbing the external service call to return the response
        stubFor(get(urlEqualTo(mockPostsUrl))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(postsMockJsonResponse)));

        // Call the API
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
//                .with(httpBasic("test_user", "test_password"))
            )
            .andDo(print())
            .andExpect(status().isOk())
        ;

        verify(1, getRequestedFor(urlEqualTo(mockPostsUrl)));
    }

    @Test
    void getPost() throws Exception {

        String mockId = "1";
        String mockPostByIdUrl = "/posts/" + mockId;

        // Read the mock response content from the file into a String
        ClassPathResource resource = new ClassPathResource("mocks/post_by_id.json");
        String postsMockJsonResponse = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        // Stubbing the external service call to return the response
        stubFor(get(urlEqualTo(mockPostByIdUrl))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(postsMockJsonResponse)));

        // Call the API
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/posts/{id}", mockId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
//                .with(httpBasic("test_user", "test_password"))
            )
            .andDo(print())
            .andExpect(status().isOk())
        ;

        verify(1, getRequestedFor(urlEqualTo(mockPostByIdUrl)));
    }

    @Test
    void getCommentsForPost() throws Exception {
        String mockPostId = "1";
        String mockCommentsUrl = "/comments";

        // Read the mock response content from the file into a String
        ClassPathResource resource = new ClassPathResource("mocks/comments_by_post_1.json");
        String commentsMockJsonResponse = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        // Stubbing the external service call to return the response
        stubFor(get(urlPathEqualTo(mockCommentsUrl))
            .withQueryParam("postId", equalTo(mockPostId))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(commentsMockJsonResponse)));

        // Call the API
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/posts/{id}/comments", mockPostId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
//                .with(httpBasic("test_user", "test_password"))
            )
            .andDo(print())
            .andExpect(status().isOk())
        ;

        verify(1, getRequestedFor(urlPathEqualTo(mockCommentsUrl))
            .withQueryParam("postId", equalTo(mockPostId)));
    }

    @Test
        // test GET "/comments" API
    void getComments() throws Exception {
        String mockPostId = "1";
        String mockCommentsUrl = "/comments";

        // Read the mock response content from the file into a String
        ClassPathResource resource = new ClassPathResource("mocks/comments.json");
        String commentsMockJsonResponse = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        // Stubbing the external service call to return the response
        stubFor(get(urlPathEqualTo(mockCommentsUrl))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(commentsMockJsonResponse)));

        // Call the API
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/comments", mockPostId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
//                .with(httpBasic("test_user", "test_password"))
            )
            .andDo(print())
            .andExpect(status().isOk())
        ;

        verify(1, getRequestedFor(urlPathEqualTo(mockCommentsUrl)));
    }

    @Test
        // test GET "/posts/{id}/comments" API
    void getCommentsForEmail() throws Exception {
        String mockEmail = "Emma@joanny.ca";
        String mockCommentsUrl = "/comments";

        // Read the mock response content from the file into a String
        ClassPathResource resource = new ClassPathResource("mocks/comments_by_email_Emma.json");
        String commentsMockJsonResponse = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        // Stubbing the external service call to return the response
        stubFor(get(urlPathEqualTo(mockCommentsUrl))
            .withQueryParam("email", equalTo(mockEmail))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(commentsMockJsonResponse)));

        // Call the API
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/comments?email={email}", mockEmail)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
//                .with(httpBasic("test_user", "test_password"))
            )
            .andDo(print())
            .andExpect(status().isOk())
        ;

        verify(1, getRequestedFor(urlPathEqualTo(mockCommentsUrl))
            .withQueryParam("email", equalTo(mockEmail)));
    }

    @Test
    void getPost_WithEmptyId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/ ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getPost_WithNonPositiveId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/0")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getPost_WithNonNumericId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getPost_WithNonExistentId_ReturnsNotFound() throws Exception {
        stubFor(get(urlPathEqualTo("/posts/999"))
            .willReturn(aResponse()
                .withStatus(404)));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/999")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(1, getRequestedFor(urlPathEqualTo("/posts/999")));
    }

    @Test
    void getPost_WithValidId_ReturnsOk() throws Exception {

        String postMockJsonResponse = """
            {
              "id": 1,
              "title": "Test Post",
              "body": "This is a test post",
              "userId": 1
            }
            """;

        stubFor(get(urlPathEqualTo("/posts/1"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(postMockJsonResponse)));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(1, getRequestedFor(urlPathEqualTo("/posts/1")));
    }



}