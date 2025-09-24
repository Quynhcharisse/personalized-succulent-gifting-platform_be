package com.exe201.group1.psgp_be.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {

    String extractEmailFromJWT(String jwt);

    String generateAccessToken(UserDetails user);

    String generateRefreshToken(UserDetails user);

    boolean checkIfNotExpired(String jwt);
}
