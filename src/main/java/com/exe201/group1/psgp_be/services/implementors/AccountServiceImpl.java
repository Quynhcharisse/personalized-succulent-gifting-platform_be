package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CreateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.ProcessAccountRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateProfileRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierStatusRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.FengShui;
import com.exe201.group1.psgp_be.enums.Role;
import com.exe201.group1.psgp_be.enums.Status;
import com.exe201.group1.psgp_be.enums.Zodiac;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.Supplier;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.SupplierRepo;
import com.exe201.group1.psgp_be.services.AccountService;
import com.exe201.group1.psgp_be.services.JWTService;
import com.exe201.group1.psgp_be.utils.CookieUtil;
import com.exe201.group1.psgp_be.utils.EntityResponseBuilder;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountServiceImpl implements AccountService {

    JWTService jwtService;
    AccountRepo accountRepo;
    SupplierRepo supplierRepo;

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
        data.put("role", account.getRole().getValue());
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

        // Nếu name không được cung cấp, tự động lấy từ email (cắt bỏ phần sau @)
        String name = request.getName();
        if (name == null || name.trim().isEmpty()) {
            String email = account.getEmail();
            if (email != null && email.contains("@")) {
                name = email.substring(0, email.indexOf("@"));
            } else {
                name = email != null ? email : "User";
            }
        }

        account.getUser().setName(name);
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
        // Name không bắt buộc, nhưng nếu có thì phải validate
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            if (request.getName().length() < 3) {
                return "Tên phải có ít nhất 3 ký tự";
            }

            if (request.getName().length() > 100) {
                return "Tên phải <= 100 ký tự";
            }
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

        if (request.getAvatarUrl() != null && !request.getAvatarUrl().trim().isEmpty()) {
            String lower = request.getAvatarUrl().toLowerCase();
            if (!(lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                    lower.endsWith(".png") || lower.endsWith(".gif") ||
                    lower.endsWith(".webp"))) {
                return "URL ảnh đại diện phải là hình ảnh (jpg, jpeg, png, gif, webp)";
            }
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

    @Override
    public ResponseEntity<ResponseObject> getAllBuyerAccounts(HttpServletRequest httpRequest) {

        List<Account> buyerList = accountRepo.findAllByRole(Role.BUYER);

        List<Map<String, Object>> body = buyerList.stream()
                .filter(buyer -> buyer.getUser() != null)
                .sorted(Comparator.comparing(Account::getRegisterDate).reversed())
                .map(
                        buyer -> {
                            Map<String, Object> data = new HashMap<>();
                            data.put("id", buyer.getId());
                            data.put("avatarUrl", buyer.getUser().getAvatarUrl());
                            data.put("email", buyer.getEmail());
                            data.put("role", buyer.getRole());
                            data.put("registerDate", buyer.getRegisterDate());
                            data.put("active", buyer.getUser().getAccount().isActive());
                            data.put("name", buyer.getUser().getName());
                            data.put("phone", buyer.getUser().getPhone());
                            data.put("gender", buyer.getUser().getGender());
                            data.put("address", buyer.getUser().getAddress());
                            return data;
                        }
                )
                .toList();

        return ResponseBuilder.build(HttpStatus.OK, "Hiển thị toàn bộ danh sách tài khoản người mua thành công", body);
    }

    @Override
    public ResponseEntity<ResponseObject> getTotalBuyerCount(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null || account.getRole() != Role.ADMIN) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Admin mới có quyền xem thống kê", null);
        }

        long totalBuyerCount = accountRepo.countByRole(Role.BUYER);

        Map<String, Object> data = new HashMap<>();
        data.put("totalBuyerCount", totalBuyerCount);

        return ResponseBuilder.build(HttpStatus.OK, "Lấy tổng số tài khoản người mua thành công", data);
    }

    @Override
    public ResponseEntity<ResponseObject> processAccount(ProcessAccountRequest request, String action) {
        Optional<Account> account = accountRepo.findById(request.getAccountId());
        if (account.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Không tìm thấy tài khoản hoặc tài khoản đã bị xóa", null);
        }

        if (action.equalsIgnoreCase("ban")) {
            account.get().setActive(false);
            accountRepo.save(account.get());
            return ResponseBuilder.build(HttpStatus.OK, "Tài khoản đã bị cấm", null); // Sửa từ BAD_REQUEST thành OK
        }

        if (action.equalsIgnoreCase("unban")) {
            account.get().setActive(true);
            accountRepo.save(account.get());
            return ResponseBuilder.build(HttpStatus.OK, "Tài khoản đã được kích hoạt", null);
        }
        return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Hành động không hợp lệ", null);
    }


    // =========================== Supplier ========================== \\
    @Override
    public ResponseEntity<ResponseObject> createSupplier(CreateSupplierRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        String error = validateCreateSupplier(request);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        supplierRepo.save(Supplier.builder().name(request.getName().trim()).contactPerson(request.getContactPerson() == null ? null : request.getContactPerson().trim()).phone(request.getPhone() == null ? null : request.getPhone().trim()).email(request.getEmail() == null ? null : request.getEmail().trim()).address(request.getAddress() == null ? null : request.getAddress().trim()).description(request.getDescription() == null ? null : request.getDescription().trim()).status(Status.ACTIVE).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build());

        return ResponseBuilder.build(HttpStatus.OK, "Tạo nhà cung cấp thành công", null);
    }

    private String validateCreateSupplier(CreateSupplierRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return "Tên nhà cung cấp là bắt buộc";
        }
        if (request.getName().length() > 100) {
            return "Tên nhà cung cấp không được vượt quá 100 ký tự";
        }
        if (supplierRepo.existsByNameIgnoreCase(request.getName())) {
            return "Nhà cung cấp với tên '" + request.getName() + "' đã tồn tại";
        }

        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String phone = request.getPhone().trim();
            if (phone.length() > 10) {
                return "Số điện thoại không được vượt quá 10 ký tự";
            }
            if (!phone.matches("^(0[3|5|7|8|9])\\d{8}$")) {
                return "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 03, 05, 07, 08 hoặc 09";
            }
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String email = request.getEmail().trim();
            if (email.length() > 100) {
                return "Email không được vượt quá 100 ký tự";
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                return "Email không đúng định dạng";
            }
        }
        if (request.getAddress() != null && request.getAddress().length() > 500) {
            return "Địa chỉ không được vượt quá 500 ký tự";
        }

        if (request.getDescription() != null && request.getDescription().length() > 500) {
            return "Mô tả không được vượt quá 500 ký tự";
        }

        return "";
    }

    @Override
    public ResponseEntity<ResponseObject> updateSupplier(UpdateSupplierRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        String error = validateUpdateSupplier(request);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        Supplier supplier = supplierRepo.findById(request.getId()).orElse(null);
        if (supplier == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy nhà cung cấp: " + request.getId(), null);
        }

        supplier.setName(request.getName().trim());
        supplier.setContactPerson(request.getContactPerson() == null ? null : request.getContactPerson().trim());
        supplier.setPhone(request.getPhone() == null ? null : request.getPhone().trim());
        supplier.setEmail(request.getEmail() == null ? null : request.getEmail().trim());
        supplier.setAddress(request.getAddress() == null ? null : request.getAddress().trim());
        supplier.setDescription(request.getDescription() == null ? null : request.getDescription().trim());
        supplier.setUpdatedAt(LocalDateTime.now());
        supplierRepo.save(supplier);

        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật nhà cung cấp thành công", null);
    }

    private String validateUpdateSupplier(UpdateSupplierRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return "Tên nhà cung cấp là bắt buộc";
        }

        if (request.getName().length() > 100) {
            return "Tên nhà cung cấp không được vượt quá 100 ký tự";
        }

        if (supplierRepo.existsByNameIgnoreCaseAndIdNot(request.getName(), request.getId())) {
            return "Nhà cung cấp với tên '" + request.getName() + "' đã tồn tại";
        }

        // Validate phone (optional but must be valid when provided)
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String phone = request.getPhone().trim();
            if (phone.length() > 10) {
                return "Số điện thoại không được vượt quá 10 ký tự";
            }
            if (!phone.matches("^(0[3|5|7|8|9])\\d{8}$")) {
                return "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 03, 05, 07, 08 hoặc 09";
            }
        }

        // Validate email (optional but must be valid when provided)
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String email = request.getEmail().trim();
            if (email.length() > 100) {
                return "Email không được vượt quá 100 ký tự";
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                return "Email không đúng định dạng";
            }
        }

        if (request.getAddress() != null && request.getAddress().length() > 500) {
            return "Địa chỉ không được vượt quá 500 ký tự";
        }

        if (request.getDescription() != null && request.getDescription().length() > 500) {
            return "Mô tả không được vượt quá 500 ký tự";
        }

        return "";
    }

    @Override
    public ResponseEntity<ResponseObject> updateSupplierStatus(UpdateSupplierStatusRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        Supplier supplier = supplierRepo.findById(request.getId()).orElse(null);
        if (supplier == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy nhà cung cấp: " + request.getId(), null);
        }

        supplier.setStatus(supplier.getStatus() == Status.ACTIVE ? Status.INACTIVE : Status.ACTIVE);
        supplier.setUpdatedAt(LocalDateTime.now());
        supplierRepo.save(supplier);

        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật thành công", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getSupplierList(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        List<Supplier> suppliers = supplierRepo.findAll().stream().sorted(Comparator.comparing(Supplier::getId).reversed()).toList();

        List<Map<String, Object>> data = suppliers.stream().map(EntityResponseBuilder::buildSupplierResponse).toList();
        return ResponseBuilder.build(HttpStatus.OK, "Lấy danh sách nhà cung cấp thành công", data);
    }

    @Override
    public ResponseEntity<ResponseObject> getTotalSupplierCount(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        long totalSupplierCount = supplierRepo.count();

        Map<String, Object> data = new HashMap<>();
        data.put("totalSupplierCount", totalSupplierCount);

        return ResponseBuilder.build(HttpStatus.OK, "Lấy tổng số nhà cung cấp thành công", data);
    }
}
