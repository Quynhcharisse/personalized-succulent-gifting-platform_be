package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.services.AccountService;
import com.exe201.group1.psgp_be.services.JWTService;
import com.exe201.group1.psgp_be.utils.CookieUtil;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
}
