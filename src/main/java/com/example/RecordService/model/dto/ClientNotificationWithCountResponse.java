package com.example.RecordService.model.dto;

import java.util.List;

public class ClientNotificationWithCountResponse {
    private List<ClientNotificationResponse> notifications;
    private long unreadCount;

    public ClientNotificationWithCountResponse() {
    }

    public ClientNotificationWithCountResponse(List<ClientNotificationResponse> notifications, long unreadCount) {
        this.notifications = notifications;
        this.unreadCount = unreadCount;
    }

    public List<ClientNotificationResponse> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<ClientNotificationResponse> notifications) {
        this.notifications = notifications;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
}

