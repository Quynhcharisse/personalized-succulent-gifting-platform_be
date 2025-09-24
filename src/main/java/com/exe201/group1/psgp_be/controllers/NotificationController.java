package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CreateNotificationRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public ResponseEntity<ResponseObject> sendNotification(@RequestBody CreateNotificationRequest request) {
        ResponseEntity<ResponseObject> response = notificationService.createNotification(request);
        messagingTemplate.convertAndSend("/topic/notifications/" + request.getAccountId(), response);
        return response;
    }

    @GetMapping()
    public ResponseEntity<ResponseObject> getNotifications(HttpServletRequest request) {
        return notificationService.getAccountNotifications(request);
    }
}