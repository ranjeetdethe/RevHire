package com.revhire.service;

import com.revhire.model.Notification;
import java.util.List;

public interface NotificationService {
    void createNotification(int userId, String message);

    List<Notification> getUserNotifications(int userId);

    long getUnreadCount(int userId);

    void markAsRead(int notificationId);

    void markAllAsRead(int userId);
}
