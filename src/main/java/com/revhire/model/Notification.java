package com.revhire.model;

import java.sql.Timestamp;

public class Notification {
    private int id;
    private int userId;
    private String message;
    private Timestamp createdAt;
    private boolean isRead;

    public Notification() {
    }

    public Notification(int id, int userId, String message, Timestamp createdAt, boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public Notification(int userId, String message) {
        this.userId = userId;
        this.message = message;
        this.isRead = false;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "Notification{id=" + id + ", userId=" + userId + ", message='" + message + "', isRead=" + isRead + "}";
    }
}
