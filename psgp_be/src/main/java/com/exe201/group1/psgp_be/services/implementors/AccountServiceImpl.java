package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.ProcessAccountRequest;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<ResponseObject> getAllBuyerAccounts() {
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Hiển thị toàn bộ danh sách tài khoản người mua thành công")
                .data(buildListBuyerAccountsDetail(accountRepo.findAllByRole(Role.BUYER)))
                .build());
    }

    @Override
    public ResponseEntity<ResponseObject> processStatusOfBuyerAccount(ProcessAccountRequest request, String action) {
        Optional<Account> account = accountRepo.findById(request.getAccountId());
        if (!account.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder().message("Không tìm thấy tài khoản hoặc tài khoản đã bị xóa")
                            .data(null).build());
        }
        if (action.equalsIgnoreCase("ban")) {
            account.get().setActive(false);
            accountRepo.save(account.get());
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Tài khoản đã bị cấm")
                    .data(buildBuyerAccountDetail(account.get())).build());
        }
        if (action.equalsIgnoreCase("unban")) {
            account.get().setActive(true);
        }
        accountRepo.save(account.get());
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Tài khoản đã được kích hoạt")
                .data(buildBuyerAccountDetail(account.get())).build());
    }


    private Map<String, Object> buildBuyerAccountDetail(Account buyerAccount){

        Map<String, Object> response = new HashMap<>();
        // Buyer Account
        response.put("accountId", buyerAccount.getId());
        response.put("email", buyerAccount.getEmail());
        response.put("registerDate", buyerAccount.getRegisterDate());
        response.put("active", buyerAccount.isActive());
        // Buyer Profile User
        if(buyerAccount.getUser() == null){
            return Collections.emptyMap();
        }
        response.put("name", buyerAccount.getUser().getName());
        response.put("phone", buyerAccount.getUser().getPhone());
        response.put("gender", buyerAccount.getUser().getGender());
        response.put("address", buyerAccount.getUser().getAddress());
        response.put("avatarUrl", buyerAccount.getUser().getAvatarUrl());
        response.put("fengShui", buyerAccount.getUser().getFengShui());
        response.put("zodiac", buyerAccount.getUser().getZodiac());
        return response;
    }

    private List<Map<String, Object>> buildListBuyerAccountsDetail(List<Account> buyerAccounts){
        List<Map<String, Object>> response = new ArrayList<>();

        List<Account> sortedBuyerAccounts = buyerAccounts.stream()
                .sorted(Comparator.comparing(Account::getRegisterDate).reversed())
                .toList();

        for(Account buyerAccount : sortedBuyerAccounts){

            Map<String, Object> buyerAccountDetail = buildBuyerAccountDetail(buyerAccount);
            if (buyerAccountDetail.isEmpty()) {
                continue;
            }
            response.add(buyerAccountDetail);

        }
        return response;
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
