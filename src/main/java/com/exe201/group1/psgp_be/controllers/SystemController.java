package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.UpdateBusinessConfigRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.SystemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/system")
@Tag(name = "System", description = "APIs for system config")
public class SystemController {

    private final SystemService systemService;

    @GetMapping("/business")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getBusinessConfig() {
        return systemService.getBusinessConfig();
    }

    @PostMapping("/business")
    @PreAuthorize("hasAnyRole('SELLER')")
    public ResponseEntity<ResponseObject> updateBusinessConfig(@RequestBody UpdateBusinessConfigRequest request) {
        return systemService.updateBusinessConfig(request);
    }
}
