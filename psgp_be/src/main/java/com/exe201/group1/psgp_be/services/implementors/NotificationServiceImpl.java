package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CreateNotificationRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.Notification;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.NotificationRepo;
import com.exe201.group1.psgp_be.services.JWTService;
import com.exe201.group1.psgp_be.services.NotificationService;
import com.exe201.group1.psgp_be.utils.CookieUtil;
import com.exe201.group1.psgp_be.utils.EntityResponseBuilder;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final AccountRepo accountRepo;
    private final JWTService jwtService;

    @Override
    public ResponseEntity<ResponseObject> createNotification(CreateNotificationRequest request) {
        int accountId = request.getAccountId();
        String message = request.getMessage();
        Account account = accountRepo.findById(accountId).orElse(null);
        if (account == null) {
            throw new IllegalArgumentException("User not found with id: " + accountId);
        }
        Notification notification = notificationRepo.save(Notification.builder()
                .account(account)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build());
        return ResponseEntity.ok(new ResponseObject("Create notification successfully", notification));
    }

    @Override
    public ResponseEntity<ResponseObject> getAccountNotifications(HttpServletRequest request) {
        Cookie access = CookieUtil.getCookie(request, "access");
        if (access == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Không có quyền truy cập", null);
        }

        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Không tìm thấy tài khoản", null);
        }

        List<Notification> notifications = notificationRepo.findByAccount(account);

        return ResponseEntity.ok(
                new ResponseObject(
                        "Get notifications successfully",
                        EntityResponseBuilder.buildNotificationsResponse(notifications)
                )
        );
    }
}
