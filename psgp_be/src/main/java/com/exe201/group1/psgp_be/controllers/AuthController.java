package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.LoginRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authService.login(request, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseObject> refresh(HttpServletRequest request, HttpServletResponse response) {
        return authService.refresh(request, response);
    }
}
