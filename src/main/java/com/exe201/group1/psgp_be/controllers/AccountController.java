package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CreateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.ProcessAccountRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateProfileRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierStatusRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
@RequestMapping("/api/v1/account")

public class AccountController {

    private final AccountService accountService;

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER', 'ADMIN')")
    public ResponseEntity<ResponseObject> logout(HttpServletRequest request, HttpServletResponse response) {
        return accountService.logout(request, response);
    }

    @PostMapping("/access")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER', 'ADMIN')")
    public ResponseEntity<ResponseObject> getAccessToken(HttpServletRequest request) {
        return accountService.getAccessToken(request);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER')")
    public ResponseEntity<ResponseObject> updateProfile(@RequestBody UpdateProfileRequest request, HttpServletRequest httpRequest) {
        return accountService.updateProfile(request, httpRequest);
    }

    @PostMapping("/profile")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER')")
    public ResponseEntity<ResponseObject> viewProfile(HttpServletRequest httpRequest) {
        return accountService.viewProfile(httpRequest);
    }

    @PostMapping("/buyer/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAllBuyerAccounts(HttpServletRequest httpRequest) {
        return accountService.getAllBuyerAccounts(httpRequest);
    }

    @GetMapping("/stats/buyer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getTotalBuyerCount(HttpServletRequest httpRequest) {
        return accountService.getTotalBuyerCount(httpRequest);
    }

    @PutMapping("/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> banAccount(@RequestBody ProcessAccountRequest request) {
        return accountService.processAccount(request, "ban");
    }

    @PutMapping("/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> unbanAccount(@RequestBody ProcessAccountRequest request) {
        return accountService.processAccount(request, "unban");
    }

    //=================== supplier =====================\\
    @PostMapping("/supplier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> createSupplier(@RequestBody CreateSupplierRequest request, HttpServletRequest httpRequest) {
        return accountService.createSupplier(request, httpRequest);
    }

    @PutMapping("/supplier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updateSupplier(@RequestBody UpdateSupplierRequest request, HttpServletRequest httpRequest) {
        return accountService.updateSupplier(request, httpRequest);
    }

    @PutMapping("/supplier/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updateSupplierStatus(@RequestBody UpdateSupplierStatusRequest request, HttpServletRequest httpRequest) {
        return accountService.updateSupplierStatus(request, httpRequest);
    }

    @GetMapping("/supplier/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getSupplierList(HttpServletRequest httpRequest) {
        return accountService.getSupplierList(httpRequest);
    }

    @GetMapping("/stats/supplier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getTotalSupplierCount(HttpServletRequest httpRequest) {
        return accountService.getTotalSupplierCount(httpRequest);
    }

}
