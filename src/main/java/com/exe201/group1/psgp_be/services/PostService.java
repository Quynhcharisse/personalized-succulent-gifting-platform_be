package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdatePostRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface PostService {

    // Define methods related to Post operations here
    ResponseEntity<ResponseObject> createPost(CreateOrUpdatePostRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> viewPosts();

    ResponseEntity<ResponseObject> viewPost(Integer id);

    ResponseEntity<ResponseObject> updatePost(Integer id, CreateOrUpdatePostRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> deletePost(Integer id, HttpServletRequest httpRequest);
}
