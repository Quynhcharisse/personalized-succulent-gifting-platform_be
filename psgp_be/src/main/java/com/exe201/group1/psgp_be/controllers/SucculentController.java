package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSucculentRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.SucculentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SucculentController {

    private final SucculentService succulentService;

    @GetMapping("/succulents")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> viewAllSucculents() {
        return succulentService.getSucculents();
    }

    @PostMapping("/succulent")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> createSucculent(@RequestBody CreateSucculentRequest request) {
        return succulentService.createSucculent(request);
    }

    @PutMapping("/succulent")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> updateSucculent(@RequestBody UpdateSucculentRequest request) {
        return succulentService.updateSucculent(request);
    }


}