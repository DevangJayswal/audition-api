package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionPost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.audition.model.Comment;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class AuditionIntegrationClient {

    private final RestTemplate restTemplate;

    @Value("${api.posts.url}")
    private String postsUrl;

    @Value("${api.comments.url}")
    private String commentsUrl;

    private final AuditionLogger auditionLogger;


    @Autowired
    public AuditionIntegrationClient(RestTemplate restTemplate, AuditionLogger auditionLogger) {
        // Create a defensive copy of the RestTemplate
        this.restTemplate = new RestTemplate(restTemplate.getRequestFactory());
        this.restTemplate.setInterceptors(new ArrayList<>(restTemplate.getInterceptors()));
        this.restTemplate.setMessageConverters(new ArrayList<>(restTemplate.getMessageConverters()));

        this.auditionLogger = auditionLogger;
    }

    public List<AuditionPost> getPosts() {
        // TODO make RestTemplate call to get Posts from https://jsonplaceholder.typicode.com/posts
        auditionLogger.info(log, "Fetching posts from {}", postsUrl);
        ResponseEntity<AuditionPost[]> response = restTemplate.getForEntity(postsUrl, AuditionPost[].class);
        AuditionPost[] posts = response.getBody();
        auditionLogger.debug(log, "Received {} posts", posts != null ? posts.length : 0);
        return posts != null ? Arrays.asList(posts) : new ArrayList<>();
    }

    public AuditionPost getPostById(final String id) {
        // TODO get post by post ID call from https://jsonplaceholder.typicode.com/posts/

        String postsByIdUrl = postsUrl + "/" + id;
        auditionLogger.info(log, "Fetching post with id {} from {}", id, postsByIdUrl);

        try {
            AuditionPost post = restTemplate.getForObject(postsByIdUrl, AuditionPost.class);
            auditionLogger.debug(log, "Successfully retrieved post with id {}", id);
            return post;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                auditionLogger.warn(log, "Post with id {} not found", id);
                throw new SystemException(
                    "Cannot find a Post with id " + id,
                    "Resource Not Found",
                    404
                );
            } else {
                auditionLogger.logErrorWithException(log, "Error occurred while fetching post with id " + id, e);
                throw new SystemException(
                    "Error occurred while fetching post with id " + id + ": " + e.getMessage(),
                    "External API Error",
                    e.getStatusCode().value(),
                    e
                );
            }
        } catch (Exception e) {
            auditionLogger.logErrorWithException(log, "Unexpected error occurred while fetching post with id " + id, e);
            throw new SystemException(
                "Unexpected error occurred while fetching post with id " + id,
                "Internal Server Error",
                500,
                e
            );
        }

        // TODO Write a method GET comments for a post from https://jsonplaceholder.typicode.com/posts/{postId}/comments - the comments must be returned as part of the post.

        // TODO write a method. GET comments for a particular Post from https://jsonplaceholder.typicode.com/comments?postId={postId}.
        // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.
    }

    public List<Comment> getComments(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(commentsUrl);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        String url = builder.toUriString();
        auditionLogger.info(log, "Fetching comments from URL: {}", url);

        try {
            ResponseEntity<List<Comment>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

            if (response.getStatusCode().is2xxSuccessful()) {
                List<Comment> comments = response.getBody();
                if (comments != null) {
                    auditionLogger.info(log, "Successfully retrieved {} comments", comments.size());
                    return comments;
                } else {
                    auditionLogger.warn(log, "Received null body when fetching comments");
                    return Collections.emptyList();
                }
            } else {
                // Handle non-2xx status codes
                auditionLogger.warn(log, "Received non-2xx status code: {} when fetching comments",
                    response.getStatusCode());
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // Handle HTTP client errors
                auditionLogger.warn(log, "Comments not found for parameters: {}", params);
            } else {
                // Other client errors
                auditionLogger.error(log, "HTTP client error when fetching comments: {}", e.getMessage());
            }
            return Collections.emptyList(); // Or throw an exception if you prefer
        } catch (Exception e) {
            // Handle other exceptions
            auditionLogger.logErrorWithException(log, "An unexpected error occurred while fetching comments", e);
            return Collections.emptyList(); // Or throw an exception
        }
    }
}

// The use of AuditionPost[] instead of List<AuditionPost> in the RestTemplate call is due to how RestTemplate handles JSON array deserialization6.
//
//When dealing with JSON arrays, RestTemplate's getForEntity() method expects an array type as the response type parameter. This is because the JSON array is naturally deserialized into a Java array by default5.
//
//Using AuditionPost[] allows for direct deserialization of the JSON array into a Java array of AuditionPost objects. If we tried to use List<AuditionPost> directly, RestTemplate wouldn't be able to perform the deserialization correctly, as it doesn't have the type information needed to create a properly typed List8.
//
//After receiving the array, we can easily convert it to a List using Arrays.asList() if needed, as shown in the getPosts() method:

// > Task :spotbugsMain
//M V EI2: new com.audition.integration.AuditionIntegrationClient(RestTemplate) may expose internal representation by storing an externally mutable object into AuditionIntegrationClient.restTemplate  At AuditionIntegrationClient.java:[line 22]

// A defensive copy is a technique used to protect an object's internal state from being modified by external code. When you create a defensive copy of an object, you ensure that any changes made to the original object do not affect the copy, and vice versa.
//
//In the context of your AuditionIntegrationClient class, making a defensive copy of the RestTemplate object means creating a new instance of RestTemplate using the same configuration as the original. This way, the RestTemplate instance used within your class is not directly exposed to external modifications.
