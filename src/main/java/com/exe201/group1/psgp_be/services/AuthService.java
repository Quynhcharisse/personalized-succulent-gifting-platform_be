package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.LoginRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<ResponseObject> login(LoginRequest request, HttpServletResponse response, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> refresh(HttpServletRequest request, HttpServletResponse response);
}
