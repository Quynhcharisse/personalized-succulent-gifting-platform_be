package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.ProcessAccountRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Lấy toàn bộ danh sách tài khoản người mua thành công")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/buyers")
    public ResponseEntity<ResponseObject> getAllBuyerAccounts() {
        return adminService.getAllBuyerAccounts();
    }

    @Operation(summary = "Lấy toàn bộ danh sách tài khoản người mua còn hoạt động thành công")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/buyers/active")
    public ResponseEntity<ResponseObject> getActiveBuyerAccounts() {
        return adminService.getActiveBuyerAccounts();
    }

    @Operation(summary = "Lấy toàn bộ danh sách tài khoản người mua bị cấm thành công")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/buyers/banned")
    public ResponseEntity<ResponseObject> getBannedBuyerAccounts() {
        return adminService.getBannedBuyerAccounts();
    }

    @Operation(summary = "Lấy thông tin tài khoản")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản hoặc tài khoản đã bị xóa"),
            @ApiResponse(responseCode = "500", description = "LỖI NGU BE: Tài khoản khi tạo không liên kết với trường trong bảng thông tin người dùng")
    })
    @GetMapping("/buyer/detail")
    public ResponseEntity<ResponseObject> getProfileBuyerAccount
            (@Parameter(description = "ID của tài khoản", required = true)
            @RequestParam int id) {
        return adminService.getProfileBuyerAccount(id);
    }

    @Operation(summary = "Cấm tài khoản")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản hoặc tài khoản đã bị xóa")
    })
    @PutMapping("/buyer/ban")
    public ResponseEntity<ResponseObject> banBuyerAccount
            (@RequestBody ProcessAccountRequest request) {
        return adminService.banBuyerAccount(request);
    }

    @Operation(summary = "Kích hoạt tài khoản")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản hoặc tài khoản đã bị xóa")
    })
    @PutMapping("/buyer/unban")
    public ResponseEntity<ResponseObject> unbanBuyerAccount( @RequestBody ProcessAccountRequest request) {
        return adminService.unbanBuyerAccount(request);
    }

}
