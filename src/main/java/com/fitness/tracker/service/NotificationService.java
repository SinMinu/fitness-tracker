package com.fitness.tracker.service;

import com.fitness.tracker.enums.NotificationType;
import com.fitness.tracker.model.Notification;
import com.fitness.tracker.model.UserSettings;
import com.fitness.tracker.repository.NotificationRepository;
import com.fitness.tracker.repository.UserRepository;
import com.fitness.tracker.repository.UserSettingsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    public Notification createNotification(Long userId, String message, NotificationType type) {
        Optional<UserSettings> userSettingsOpt = userSettingsRepository.findByUserId(userId);

        if (userSettingsOpt.isEmpty()) {
            throw new IllegalArgumentException("User settings not found for user ID: " + userId);
        }

        UserSettings userSettings = userSettingsOpt.get();

        boolean shouldCreateNotification = switch (type) {
            case GOAL_ACHIEVEMENT -> userSettings.isGoalAchievementAlert();
            case APPROACHING_GOAL_END -> userSettings.isApproachingGoalEndAlert();
            case GOAL_END -> userSettings.isGoalEndAlert();
            default -> true;
        };

        if (shouldCreateNotification) {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setMessage(message);
            notification.setType(type);  // type 설정
            return notificationRepository.save(notification);
        }
        return null;  // 조건에 맞지 않으면 알림 생성하지 않음
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