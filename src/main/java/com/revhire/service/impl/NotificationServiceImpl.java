package com.revhire.service.impl;

import com.revhire.dao.NotificationDAO;
import com.revhire.dao.impl.NotificationDAOImpl;
import com.revhire.model.Notification;
import com.revhire.service.NotificationService;
import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO;

    public NotificationServiceImpl() {
        this.notificationDAO = new NotificationDAOImpl();
    }

    public NotificationServiceImpl(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    @Override
    public void sendNotification(int userId, String message) {
        Notification notification = new Notification(userId, message);
        notificationDAO.createNotification(notification);
    }

    @Override
    public List<Notification> getUserNotifications(int userId) {
        return notificationDAO.findByUserId(userId);
    }

    @Override
    public void markAsRead(int notificationId) {
        notificationDAO.markAsRead(notificationId);
    }

    @Override
    public int getUnreadCount(int userId) {
        return notificationDAO.getUnreadCount(userId);
    }
}
