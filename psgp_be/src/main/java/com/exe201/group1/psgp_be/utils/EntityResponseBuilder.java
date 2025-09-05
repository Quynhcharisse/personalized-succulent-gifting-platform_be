package com.exe201.group1.psgp_be.utils;

import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.Notification;
import com.exe201.group1.psgp_be.models.User;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EntityResponseBuilder {

    //-------Account---------//

    public static Map<String, Object> buildAccountResponse(Account account) {
        List<String> keys = List.of(
                "id", "email", "role",
                "registerDate", "active", "user"
        );
        List<Object> values = List.of(
                account.getId(),
                Objects.requireNonNullElse(account.getEmail(), ""),
                Objects.requireNonNullElse(account.getRole(), ""),
                Objects.requireNonNullElse(account.getRegisterDate(), ""),
                account.isActive(),
                Objects.requireNonNullElse(buildUserResponse(account.getUser()), "")
        );
        return MapUtils.build(keys, values);
    }

    //-------Customer---------//
    public static Map<String, Object> buildUserResponse(User user) {
        if (user == null) {
            return null;
        }

        List<String> keys = List.of(
                "id", "name", "phone", "gender",
                "address", "avatarUrl", "fengShui", "zodiac"
        );
        List<Object> values = List.of(
                user.getId(),
                Objects.requireNonNullElse(user.getName(), ""),
                Objects.requireNonNullElse(user.getPhone(), ""),
                Objects.requireNonNullElse(user.getGender(), ""),
                Objects.requireNonNullElse(user.getAddress(), ""),
                Objects.requireNonNullElse(user.getAvatarUrl(), ""),
                Objects.requireNonNullElse(user.getFengShui(), ""),
                Objects.requireNonNullElse(user.getZodiac(), "")
        );

        return MapUtils.build(keys, values);
    }

    public static  Map<String, Object> buildNotificationsResponse(List<Notification> notifications) {
        return Map.of(
                "count", notifications.size(),
                "notifications", notifications.stream().map(notification -> Map.of(
                        "id", notification.getId(),
                        "message", notification.getMessage(),
                        "isRead", notification.isRead(),
                        "createdAt", notification.getCreatedAt(),
                        "accountId", notification.getAccount().getId()
                )).toList()
        );
    }

}
