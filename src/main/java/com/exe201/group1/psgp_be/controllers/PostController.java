package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdatePostRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class PostController {
    private final PostService postService;

    @PostMapping()
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> createPost(
            @RequestBody @Valid CreateOrUpdatePostRequest request,
            HttpServletRequest httpRequest
    ) {
        return postService.createPost(request, httpRequest);
    }

    @GetMapping()
    public ResponseEntity<ResponseObject> viewPosts() {
        return postService.viewPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> viewPost(@PathVariable Integer id) {
        return postService.viewPost(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> updatePost(
            @PathVariable Integer id,
            @RequestBody @Valid CreateOrUpdatePostRequest request,
            HttpServletRequest httpRequest
    ) {
        return postService.updatePost(id, request, httpRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> deletePost(
            @PathVariable Integer id,
            HttpServletRequest httpRequest
    ) {
        return postService.deletePost(id, httpRequest);
    }

}
