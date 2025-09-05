package com.exe201.group1.psgp_be.services.implementors;


import com.exe201.group1.psgp_be.dto.requests.CreateNotificationRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.Notification;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.NotificationRepo;
import com.exe201.group1.psgp_be.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final AccountRepo accountRepo;

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
    public ResponseEntity<ResponseObject> getAccountNotifications(Integer accountId) {
        Account account = accountRepo.findById(accountId).orElse(null);
        if (account == null) {
            throw new IllegalArgumentException("User not found with id: " + accountId);
        }
        List<Notification> notifications = notificationRepo.findByAccount(account);
        return ResponseEntity.ok(new ResponseObject("Get notifications successfully", notifications));
    }
}
