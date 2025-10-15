package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdateCommentRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdatePostRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Tag(name = "Post", description = "APIs for Post")
public class PostController {
    private final PostService postService;

    @PostMapping()
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Create a new post (Seller only)")
    public ResponseEntity<ResponseObject> createPost(
            @RequestBody @Valid CreateOrUpdatePostRequest request,
            HttpServletRequest httpRequest
    ) {
        return postService.createPost(request, httpRequest);
    }

    @GetMapping()
    @Operation(summary = "View all posts")
    public ResponseEntity<ResponseObject> viewPosts() {
        return postService.viewPosts();
    }

    @GetMapping("/seller")
    @Operation(summary = "View all posts by a specific seller")
    public ResponseEntity<ResponseObject> viewPostsBySeller(HttpServletRequest httpRequest) {
        return postService.viewPostsBySeller(httpRequest);
    }

    @GetMapping("/{id}")
    @Operation(summary = "View a post by ID")
    public ResponseEntity<ResponseObject> viewPost(@PathVariable Integer id) {
        return postService.viewPost(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Update a post by ID (Seller only)")
    public ResponseEntity<ResponseObject> updatePost(
            @PathVariable Integer id,
            @RequestBody @Valid CreateOrUpdatePostRequest request,
            HttpServletRequest httpRequest
    ) {
        return postService.updatePost(id, request, httpRequest);
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Create a comment on a post")
    public ResponseEntity<ResponseObject> createPostComment(
            @PathVariable Integer id,
            @RequestBody @Valid CreateOrUpdateCommentRequest request,
            HttpServletRequest httpRequest
    ) {
        return postService.createPostComment(id, request, httpRequest);
    }

    @PutMapping("/comments/{id}")
    @Operation(summary = "Update a comment by ID")
    public ResponseEntity<ResponseObject> updatePostComment(
            @PathVariable Integer id,
            @RequestBody @Valid CreateOrUpdateCommentRequest request,
            HttpServletRequest httpRequest
    ) {
        return postService.updatePostComment(id, request, httpRequest);
    }

    @GetMapping("/tags")
    @Operation(summary = "Get all post tags")
    public ResponseEntity<ResponseObject> getPostTags() {
        return postService.getPostTags();
    }

}
