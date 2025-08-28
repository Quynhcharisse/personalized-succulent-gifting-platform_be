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
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Đăng xuất thất bại", null);
        }

        if (!jwtService.checkIfNotExpired(refresh.getValue())) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Token không hợp lệ", null);
        }

        CookieUtil.removeCookies(response);

        return ResponseBuilder.build(HttpStatus.OK, "Đăng xuất thành công", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getAccessToken(HttpServletRequest request) {
        Cookie access = CookieUtil.getCookie(request, "access");
        if (access == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Không có quyền truy cập", null);
        }

        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Không tìm thấy tài khoản", null);
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
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Tài khoản không hợp lệ", null);
        }

        if (account.getUser() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Không tìm thấy thông tin người dùng", null);
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
        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật thông tin thành công", null);
    }

    private String UpdateProfileValidation(UpdateProfileRequest request) {

        if (request.getName() == null || request.getName().isEmpty()) {
            return "Tên là bắt buộc";
        }

        if (request.getName().length() < 3) {
            return "Tên phải có ít nhất 3 ký tự";
        }

        if (request.getName().length() > 100) {
            return "Tên phải <= 100 ký tự";
        }

        if (request.getPhone() == null || request.getPhone().isEmpty()) {
            return "Số điện thoại là bắt buộc";
        }

        // Regex cho số điện thoại VN: bắt đầu bằng 0 hoặc +84, tổng cộng 10–11 số
        if (!request.getPhone().matches("^(0[1-9][0-9]{8,9}|\\+84[1-9][0-9]{7,9})$")) {
            return "Định dạng số điện thoại không hợp lệ";
        }

        if (request.getGender() == null || request.getGender().isEmpty()) {
            return "Giới tính là bắt buộc";
        }

        String gender = request.getGender().toUpperCase();
        if (!(gender.equals("MALE") || gender.equals("FEMALE"))) {
            return "Giới tính phải là một trong: MALE, FEMALE";
        }

        if (request.getAddress() == null || request.getAddress().isEmpty()) {
            return "Địa chỉ là bắt buộc";
        }

        if (request.getAddress().length() > 255) {
            return "Địa chỉ phải <= 255 ký tự";
        }

        if (request.getAvatarUrl() == null || request.getAvatarUrl().isEmpty()) {
            return "URL ảnh đại diện là bắt buộc";
        }

        String lower = request.getAvatarUrl().toLowerCase();
        if (!(lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                lower.endsWith(".png") || lower.endsWith(".gif") ||
                lower.endsWith(".webp"))) {
            return "URL ảnh đại diện phải là hình ảnh (jpg, jpeg, png, gif, webp)";
        }

        return "";
    }

    @Override
    public ResponseEntity<ResponseObject> viewProfile(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Tài khoản không hợp lệ", null);
        }

        return ResponseBuilder.build(HttpStatus.OK, "Lấy thông tin hồ sơ thành công", EntityResponseBuilder.buildAccountResponse(account));
    }
}
