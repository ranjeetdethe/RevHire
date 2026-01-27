package com.revhire.service;

import com.revhire.dao.NotificationDAO;
import com.revhire.model.Notification;
import com.revhire.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class NotificationServiceTest {

    // Stub DAO for testing
    class NotificationDAOStub implements NotificationDAO {
        List<Notification> store = new ArrayList<>();

        @Override
        public void createNotification(Notification notification) {
            store.add(notification);
        }

        @Override
        public List<Notification> findByUserId(int userId) {
            List<Notification> result = new ArrayList<>();
            for (Notification n : store) {
                if (n.getUserId() == userId)
                    result.add(n);
            }
            return result;
        }

        @Override
        public void markAsRead(int notificationId) {
            for (Notification n : store) {
                if (n.getId() == notificationId)
                    n.setRead(true);
            }
        }

        @Override
        public int getUnreadCount(int userId) {
            int count = 0;
            for (Notification n : store) {
                if (n.getUserId() == userId && !n.isRead())
                    count++;
            }
            return count;
        }
    }

    @Test
    public void testSendNotification() {
        NotificationDAOStub stub = new NotificationDAOStub();
        NotificationService service = new NotificationServiceImpl(stub);

        service.sendNotification(1, "Test Message");

        List<Notification> notes = service.getUserNotifications(1);
        Assertions.assertEquals(1, notes.size());
        Assertions.assertEquals("Test Message", notes.get(0).getMessage());
    }

    @Test
    public void testUnreadCount() {
        NotificationDAOStub stub = new NotificationDAOStub();
        NotificationService service = new NotificationServiceImpl(stub);

        service.sendNotification(1, "Msg 1");
        service.sendNotification(1, "Msg 2");

        Assertions.assertEquals(2, service.getUnreadCount(1));
    }
}
