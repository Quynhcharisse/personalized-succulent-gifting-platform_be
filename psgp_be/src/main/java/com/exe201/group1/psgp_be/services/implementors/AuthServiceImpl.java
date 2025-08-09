package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.LoginRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.Role;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.AccountRequest;
import com.exe201.group1.psgp_be.models.User;
import com.exe201.group1.psgp_be.models.Wishlist;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.AccountRequestRepo;
import com.exe201.group1.psgp_be.repositories.UserRepo;
import com.exe201.group1.psgp_be.repositories.WishlistRepo;
import com.exe201.group1.psgp_be.services.AuthService;
import com.exe201.group1.psgp_be.services.JWTService;
import com.exe201.group1.psgp_be.utils.CookieUtil;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.expiration.access-token}")
    private long accessExpiration;

    @Value("${jwt.expiration.refresh-token}")
    private long refreshExpiration;

    private final AccountRepo accountRepo;

    private final AccountRequestRepo accountRequestRepo;

    private final UserRepo userRepo;

    private final WishlistRepo wishlistRepo;

    private final JWTService jwtService;

    @Override
    public ResponseEntity<ResponseObject> login(LoginRequest request, HttpServletResponse response) {
        Account account = accountRepo.findByEmail(request.getEmail()).orElse(null);
        AccountRequest accountRequest = accountRequestRepo.findByEmail(request.getEmail()).orElse(null);

        if (accountRequest != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "This email is requested to be a partner", null);
        }

        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Email is required", null);
        }

        if (account == null) {
            return register(request, response);
        }

        if (!account.isActive()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account is banned", null);
        }

        String access = jwtService.generateAccessToken(account);
        String refresh = jwtService.generateRefreshToken(account);

        CookieUtil.createCookies(response, access, refresh, accessExpiration, refreshExpiration);

        return ResponseBuilder.build(HttpStatus.OK, "Login successfully", buildAccountData(account));
    }


    private ResponseEntity<ResponseObject> register(LoginRequest request, HttpServletResponse response) {
        // Create Account
        Account account = accountRepo.save(
                Account.builder()
                        .email(request.getEmail())
                        .role(Role.BUYER)
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        // Create User
        User user = userRepo.save(
                User.builder()
                        .account(account)
                        .name("N/A")
                        .phone("N/A")
                        .gender("N/A")
                        .address("N/A")
                        .avatarUrl("N/A")
                        .build()
        );

        // Create Wishlist for the user
        Wishlist wishlist = wishlistRepo.save(
                Wishlist.builder()
                        .buyer(user)
                        .version(1)
                        .build()
        );

        // Set the user in account and save
        account.setUser(user);
        accountRepo.save(account);

        String access = jwtService.generateAccessToken(account);
        String refresh = jwtService.generateRefreshToken(account);

        CookieUtil.createCookies(response, access, refresh, accessExpiration, refreshExpiration);

        return ResponseBuilder.build(HttpStatus.OK, "Login successfully", buildAccountData(account));
    }

    private Map<String, Object> buildAccountData(Account account) {
        Map<String, Object> accountData = new HashMap<>();
        accountData.put("email", account.getEmail());
        accountData.put("createdAt", account.getCreatedAt());
        accountData.put("role", account.getRole());
        if (!account.getRole().equals(Role.ADMIN)) {
            accountData.put("user", buildUserData(account));
        }
        return accountData;
    }

    private Map<String, Object> buildUserData(Account account) {
        User user = account.getUser();
        if (user == null) {
            return null;
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.getName());
        userData.put("phone", user.getPhone());
        userData.put("gender", user.getGender());
        userData.put("address", user.getAddress());
        userData.put("avatarUrl", user.getAvatarUrl());
        userData.put("fengShui", user.getFengShui());
        userData.put("zodiac", user.getZodiac());
        return userData;
    }

    @Override
    public ResponseEntity<ResponseObject> refresh(HttpServletRequest request, HttpServletResponse response) {
        Account currentAcc = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);

        if (currentAcc == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "No user found", null);
        }

        String newAccess = jwtService.generateAccessToken(currentAcc);

        String newRefresh = jwtService.generateRefreshToken(currentAcc);

        CookieUtil.createCookies(response, newAccess, newRefresh, accessExpiration, refreshExpiration);

        return ResponseBuilder.build(HttpStatus.OK, "Refresh access token successfully", null);
    }
}
