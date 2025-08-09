package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AccountService {
    ResponseEntity<ResponseObject> logout(HttpServletRequest request, HttpServletResponse response);
}
