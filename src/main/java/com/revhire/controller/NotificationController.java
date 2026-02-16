package com.revhire.controller;

import com.revhire.model.Notification;
import com.revhire.model.User;
import com.revhire.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String viewNotifications(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        List<Notification> notifications = notificationService.getUserNotifications(user.getId());
        model.addAttribute("notifications", notifications);

        return "notifications";
    }

    @GetMapping("/api/unread-count")
    @ResponseBody
    public Map<String, Long> getUnreadCount(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return Map.of("count", 0L);
        return Map.of("count", notificationService.getUnreadCount(user.getId()));
    }

    @PostMapping("/mark-read/{id}")
    @ResponseBody
    public ResponseEntity<?> markRead(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return ResponseEntity.status(401).build();

        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-all-read")
    public String markAllRead(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            notificationService.markAllAsRead(user.getId());
        }
        return "redirect:/notifications";
    }
}
