package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.models.Notification;
import java.util.List;

public interface NotificationService {

    public Notification createNotification(Integer accountId, String message);

    public List<Notification> getAccountNotifications(Integer accountId);
}
