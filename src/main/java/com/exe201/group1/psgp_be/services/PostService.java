package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdateCommentRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdatePostRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface PostService {

    ResponseEntity<ResponseObject> createPost(CreateOrUpdatePostRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> viewPosts();

    ResponseEntity<ResponseObject> viewPost(Integer id);

    ResponseEntity<ResponseObject> updatePost(Integer id, CreateOrUpdatePostRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> deletePost(Integer id, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> createPostComment(Integer postId, CreateOrUpdateCommentRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> updatePostComment(Integer postId, CreateOrUpdateCommentRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> deletePostComment(Integer commentId, HttpServletRequest httpRequest);
}
