package com.exe201.group1.psgp_be.utils;

import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.services.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

public class CookieUtil {
    public static Cookie getCookie(@NonNull HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static void createCookies(HttpServletRequest request, HttpServletResponse response, String accessValue, String refreshValue, long accessExp, long refreshExp) {

        String accessCookie = "";

        String refreshCookie = "";

        if(request.isSecure()){
            accessCookie = String.format("access=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=None; Secure", accessValue, accessExp / 1000);

            refreshCookie = String.format("refresh=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=None; Secure", refreshValue, refreshExp / 1000);
        }else {
            accessCookie = String.format("access=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Lax", accessValue, accessExp / 1000);

            refreshCookie = String.format("refresh=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Lax", refreshValue, refreshExp / 1000);
        }
        response.addHeader("Set-Cookie", accessCookie);

        response.addHeader("Set-Cookie", refreshCookie);
    }

    public static void removeCookies(HttpServletResponse response) {
        // Xóa cookie "access"
        response.addHeader("Set-Cookie", "access=; Path=/; Max-Age=0; HttpOnly; SameSite=None; Secure");

        // Xóa cookie "refresh"
        response.addHeader("Set-Cookie", "refresh=; Path=/; Max-Age=0; HttpOnly; SameSite=None; Secure");
    }

    public static Account extractAccountFromCookie(HttpServletRequest request, JWTService jwtService, AccountRepo accountRepo) {
        Cookie cookie = CookieUtil.getCookie(request, "refresh");
        if (cookie == null) {
            return null;
        }

        String refreshToken = cookie.getValue();
        String email = jwtService.extractEmailFromJWT(refreshToken);

        return accountRepo.findByEmailAndActive(email, true).orElse(null);

    }
}
