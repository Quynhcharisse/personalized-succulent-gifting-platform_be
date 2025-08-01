package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.models.Account;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {

    String extractEmailFromJWT(String jwt);

    Account extractAccountFromCookie(HttpServletRequest request);

    String generateAccessToken(UserDetails user);

    String generateRefreshToken(UserDetails user);

    boolean checkIfNotExpired(String jwt);

    String generateVerifyToken(String email);
}
