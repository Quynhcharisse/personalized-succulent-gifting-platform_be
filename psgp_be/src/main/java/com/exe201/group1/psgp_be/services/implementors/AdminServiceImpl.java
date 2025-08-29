package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.Role;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AccountRepo accountRepo;

    @Override
    public ResponseEntity<ResponseObject> getAllBuyerAccounts() {
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Hiển thị toàn bộ danh sách tài khoản người mua thành công")
                .data(buildListBuyerAccountsDetail(accountRepo.findAllByRole(Role.BUYER)))
                .build());
    }
    @Override
    public ResponseEntity<ResponseObject> getActiveBuyerAccounts(){
        return ResponseEntity.ok(ResponseObject.builder()
                        .message("Hiển thị danh sách tài khoản người mua còn hoạt động thành công")
                        .data(buildListBuyerAccountsDetail(accountRepo.findByActiveAndRole(true, Role.BUYER)))
                .build());
    }

    @Override
    public ResponseEntity<ResponseObject> getBannedBuyerAccounts() {
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Hiển thị danh sách tài khoản người mua bị cấm thành công")
                .data(buildListBuyerAccountsDetail(accountRepo.findByActiveAndRole(false, Role.BUYER)))
                .build());
    }

    @Override
    public ResponseEntity<ResponseObject> getProfileBuyerAccount(int id){
        Optional<Account> account = accountRepo.findById(id);
        if(!account.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder().message("Không tìm thấy tài khoản hoặc tài khoản đã bị xóa")
                            .data(null).build());
        }
        if(account.get().getUser() == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder().message("LỖI NGU BE: Tài khoản khi tạo không liên kết với trường trong bảng thông tin người dùng")
                            .data(null).build());
        }
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Hiển thị thông tin tài khoản người mua thành công")
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
                .sorted(Comparator.comparing(Account::getRegisterDate))
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

}
