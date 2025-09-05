package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CreateNotificationRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface NotificationService {

    ResponseEntity<ResponseObject> createNotification(CreateNotificationRequest request);

    ResponseEntity<ResponseObject> getAccountNotifications(HttpServletRequest request);
}
