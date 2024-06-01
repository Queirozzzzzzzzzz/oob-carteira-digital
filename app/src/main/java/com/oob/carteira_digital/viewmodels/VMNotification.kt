package com.oob.carteira_digital.viewmodels

import com.oob.carteira_digital.models.Notification

class VMNotification() {
    private var notification = Notification();

    suspend fun getNotifications(): List<String> {
        return notification.getNotifications();
    }
}