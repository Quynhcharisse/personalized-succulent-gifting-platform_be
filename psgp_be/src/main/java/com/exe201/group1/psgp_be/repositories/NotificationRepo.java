package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification, String> {
    List<Notification> findByAccount(Account account);
}
