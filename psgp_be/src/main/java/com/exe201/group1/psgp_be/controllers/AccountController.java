package com.exe201.group1.psgp_be.controllers;


import com.exe201.group1.psgp_be.dto.requests.ProcessAccountRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateProfileRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.AccountService;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/buyers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAllBuyerAccounts() {
        return accountService.getAllBuyerAccounts();
    }

    @PutMapping("/buyer/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> banBuyerAccount
            (@RequestBody ProcessAccountRequest request) {
        return accountService.processStatusOfBuyerAccount(request, "ban");
    }

    @PutMapping("/buyer/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> unbanBuyerAccount( @RequestBody ProcessAccountRequest request) {
        return accountService.processStatusOfBuyerAccount(request, "unban");
    }


}
