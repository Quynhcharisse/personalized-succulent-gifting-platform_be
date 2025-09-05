package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.models.Notification;
import com.exe201.group1.psgp_be.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public Notification sendNotification(@RequestParam Integer accountId, @RequestParam String message) {
        Notification notification = notificationService.createNotification(accountId, message);
        messagingTemplate.convertAndSend("/topic/notifications/" + accountId, notification);
        return notification;
    }
}