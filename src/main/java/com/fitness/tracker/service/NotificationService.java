package com.fitness.tracker.service;

import com.fitness.tracker.model.Notification;
import com.fitness.tracker.repository.NotificationRepository;
import com.fitness.tracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    public Notification createNotification(Long userId, String message) {
        // userId 검증 로직 추가 필요 (예: 유저 존재 여부 확인)
        if (userId == null || userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("Invalid userId provided");
        }

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        return notificationRepository.save(notification);
    }


    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }

}