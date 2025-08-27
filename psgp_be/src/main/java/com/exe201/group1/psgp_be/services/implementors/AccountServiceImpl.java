package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.UpdateProfileRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.FengShui;
import com.exe201.group1.psgp_be.enums.Role;
import com.exe201.group1.psgp_be.enums.Zodiac;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.services.AccountService;
import com.exe201.group1.psgp_be.services.JWTService;
import com.exe201.group1.psgp_be.utils.CookieUtil;
import com.exe201.group1.psgp_be.utils.EntityResponseBuilder;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final JWTService jwtService;
    private final AccountRepo accountRepo;

    @Override
    public ResponseEntity<ResponseObject> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie refresh = CookieUtil.getCookie(request, "refresh");
        if (refresh == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Logout failed", null);
        }

        if (!jwtService.checkIfNotExpired(refresh.getValue())) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Token invalid", null);
        }

        CookieUtil.removeCookies(response);

        return ResponseBuilder.build(HttpStatus.OK, "Logout successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getAccessToken(HttpServletRequest request) {
        Cookie access = CookieUtil.getCookie(request, "access");
        if (access == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "No access", null);
        }

        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "No account", null);
        }


        Map<String, Object> data = new HashMap<>();
        data.put("access", access.getValue());
        data.put("id", account.getId());
        data.put("email", account.getEmail());
        data.put("role", account.getRole());
        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> updateProfile(UpdateProfileRequest request, HttpServletRequest httpRequest) {
        String error = UpdateProfileValidation(request);

        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid account", null);
        }

        if (account.getUser() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "User profile not found", null);
        }

        account.getUser().setName(request.getName());
        account.getUser().setPhone(request.getPhone());
        account.getUser().setAddress(request.getAddress());
        account.getUser().setGender(request.getGender());
        account.getUser().setAvatarUrl(request.getAvatarUrl());

        if (Role.BUYER.equals(account.getRole())) {
            account.getUser().setFengShui(FengShui.valueOf(request.getFengShui()));
            account.getUser().setZodiac(Zodiac.valueOf(request.getZodiac()));
        }

        accountRepo.save(account);
        return ResponseBuilder.build(HttpStatus.OK, "Update information successfully", null);
    }

    private String UpdateProfileValidation(UpdateProfileRequest request) {

        if (request.getName() == null || request.getName().isEmpty()) {
            return "Name is required";
        }

        if (request.getName().length() < 3) {
            return "Name must be at least 3 characters";
        }

        if (request.getName().length() > 100) {
            return "Name must be <= 100 characters";
        }

        if (request.getPhone() == null || request.getPhone().isEmpty()) {
            return "Phone is required";
        }

        // Regex cho số điện thoại VN: bắt đầu bằng 0 hoặc +84, tổng cộng 10–11 số
        if (!request.getPhone().matches("^(0[1-9][0-9]{8,9}|\\+84[1-9][0-9]{7,9})$")) {
            return "Phone format is invalid";
        }

        if (request.getGender() == null || request.getGender().isEmpty()) {
            return "Gender is required";
        }

        String gender = request.getGender().toUpperCase();
        if (!(gender.equals("MALE") || gender.equals("FEMALE"))) {
            return "Gender must be one of: MALE, FEMALE";
        }

        if (request.getAddress() == null || request.getAddress().isEmpty()) {
            return "Address is required";
        }

        if (request.getAddress().length() > 255) {
            return "Address must be <= 255 characters";
        }

        if (request.getAvatarUrl() == null || request.getAvatarUrl().isEmpty()) {
            return "AvatarUrl is required";
        }

        String lower = request.getAvatarUrl().toLowerCase();
        if (!(lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                lower.endsWith(".png") || lower.endsWith(".gif") ||
                lower.endsWith(".webp"))) {
            return "AvatarUrl must be an image (jpg, jpeg, png, gif, webp)";
        }

        return "";
    }

    @Override
    public ResponseEntity<ResponseObject> viewProfile(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid account", null);
        }

        return ResponseBuilder.build(HttpStatus.OK, "Get profile successfully", EntityResponseBuilder.buildAccountResponse(account));
    }
}
