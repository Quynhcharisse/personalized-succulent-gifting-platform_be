package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.DeleteCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.CustomRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/custom")
@Tag(name = "Custom Request", description = "APIs for custom request")
public class CustomRequestController {

    private final CustomRequestService customRequestService;

    //=================== custom request =====================\\
    @GetMapping("/custom/requests")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> customRequestListByBuyer(HttpServletRequest request) {
        return customRequestService.customRequestListByBuyer(request);
    }

    @PostMapping("/custom/requests")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> createCustomRequest(@RequestBody CreateCustomRequest request) {
        return customRequestService.createCustomRequest(request);
    }

    @PutMapping("/custom/requests")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> updateCustomRequest(@RequestBody UpdateCustomRequestRequest request) {
        return customRequestService.updateCustomRequest(request);
    }

    @DeleteMapping("/custom/requests")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> deleteCustomRequest(@RequestBody DeleteCustomRequestRequest request) {
        return customRequestService.deleteCustomRequest(request);
    }

}
