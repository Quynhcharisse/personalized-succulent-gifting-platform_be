package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CreateCustomProductRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateRevisionRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomProductRequestDesignImageRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.CustomRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/custom")
@Tag(name = "Custom Request", description = "APIs for custom request")
public class CustomRequestController {

    private final CustomRequestService customRequestService;

    //=================== Custom Product =====================\\
    @PostMapping("/custom-request")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> createCustomProductRequest(
            @RequestBody CreateCustomProductRequestRequest request,
            HttpServletRequest httpRequest
    ) {
        return customRequestService.createCustomProductRequest(request, httpRequest);
    }

    @PutMapping("/custom-request/revision")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> createRevision(@RequestBody CreateRevisionRequest request) {
        return customRequestService.createRevision(request);
    }

    @PostMapping("/custom-request/list")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> viewCustomProductRequest(HttpServletRequest request) {
        return customRequestService.viewCustomProductRequest();
    }

    @GetMapping("/custom-request/list")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> viewCustomProductRequest() {
        return customRequestService.viewCustomProductRequest();
    }

    @GetMapping("/custom-request/list/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> viewCustomProductRequestDetail(@PathVariable int id) {
        return customRequestService.viewCustomProductRequestDetail(id);
    }

    @PutMapping("/custom-request/design-image")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> updateCustomProductRequestDesignImage(
            @RequestBody UpdateCustomProductRequestDesignImageRequest request,
            @RequestParam(name = "a", defaultValue = "true") String approved
    ) {
        return customRequestService.updateCustomProductRequestDesignImage(request, approved.equalsIgnoreCase("true"));
    }
}
