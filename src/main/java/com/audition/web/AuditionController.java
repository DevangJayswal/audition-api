package com.audition.web;

import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuditionController {

    private final AuditionService auditionService;

    // TODO Add a query param that allows data filtering. The intent of the filter is at developers discretion.
    @GetMapping("/posts")
    public ResponseEntity<List<AuditionPost>> getPosts(@RequestParam(required = false) Integer userId,
        @RequestParam(required = false) Integer id,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String body) {

        // TODO Add logic that filters response data based on the query param
        List<AuditionPost> posts = auditionService.getPosts();

        posts = posts.stream()
            .filter(post -> (userId == null || post.getUserId() == userId)
                && (id == null || post.getId() == id)
                && (title == null || post.getTitle().toLowerCase().contains(title.toLowerCase()))
                && (body == null || post.getBody().toLowerCase().contains(body.toLowerCase())))
            .collect(Collectors.toList());

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<AuditionPost> getPost(@PathVariable("id") final String postId) {
        // TODO Add input validation

        if (postId == null || postId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            int id = Integer.parseInt(postId);
            if (id <= 0) {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(null);
        }

        AuditionPost auditionPost = auditionService.getPostById(postId);

        if (auditionPost == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(auditionPost);
    }

    // TODO Add additional methods to return comments for each post. Hint: Check https://jsonplaceholder.typicode.com/

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<Comment>> getCommentsForPost(@PathVariable String postId) {
        if (postId == null || postId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, String> params = Collections.singletonMap("postId", postId);
        List<Comment> comments = auditionService.getComments(params);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/comments")
    public ResponseEntity<List<Comment>> getComments(
        @RequestParam(required = false) String postId,
        @RequestParam(required = false) String id,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String body
    ) {
        Map<String, String> params = new HashMap<>();
        if (postId != null) {
            params.put("postId", postId);
        }
        if (id != null) {
            params.put("id", id);
        }
        if (name != null) {
            params.put("name", name);
        }
        if (email != null) {
            params.put("email", email);
        }
        if (body != null) {
            params.put("body", body);
        }

        List<Comment> comments = auditionService.getComments(params);
        return ResponseEntity.ok(comments);
    }

}
