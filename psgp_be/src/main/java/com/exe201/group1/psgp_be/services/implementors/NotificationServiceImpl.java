package com.exe201.group1.psgp_be.services.implementors;


import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.Notification;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.NotificationRepo;
import com.exe201.group1.psgp_be.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepository;
    private final AccountRepo accountRepo;

    @Override
    public Notification createNotification(Integer accountId, String message) {
        Account account = accountRepo.findById(accountId).orElse(null);
        if (account == null) {
            throw new IllegalArgumentException("User not found with id: " + accountId);
        }
        Notification notification = Notification.builder()
                .account(account)
                .message(message)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getAccountNotifications(Integer accountId) {
        Account account = accountRepo.findById(accountId).orElse(null);
        if (account == null) {
            throw new IllegalArgumentException("User not found with id: " + accountId);
        }
        return notificationRepository.findByAccount(account);
    }
}
