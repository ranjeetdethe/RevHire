package com.revhire.dao;

import com.revhire.model.Notification;
import java.util.List;

public interface NotificationDAO {
    void createNotification(Notification notification);

    List<Notification> findByUserId(int userId);

    void markAsRead(int notificationId);

    int getUnreadCount(int userId);
}
