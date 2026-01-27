package com.revhire.service;

import com.revhire.model.Notification;
import java.util.List;

public interface NotificationService {
    void sendNotification(int userId, String message);

    List<Notification> getUserNotifications(int userId);

    void markAsRead(int notificationId);

    int getUnreadCount(int userId);
}
