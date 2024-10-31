package com.fitness.tracker.service;

import com.fitness.tracker.model.User;
import com.fitness.tracker.model.UserSettings;
import com.fitness.tracker.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    public UserSettings getUserSettings(Long userId) {
        return userSettingsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserSettings newSettings = new UserSettings();
                    newSettings.setUser(new User(userId)); // User 엔티티와 연결 설정
                    newSettings.setGoalAchievementAlert(true);
                    newSettings.setApproachingGoalEndAlert(true);
                    newSettings.setGoalEndAlert(true);
                    return userSettingsRepository.save(newSettings); // 저장 후 반환
                });
    }

    public UserSettings updateUserSettings(Long userId, UserSettings updatedSettings) {
        // Optional 처리하여 기존 설정이 있는지 확인
        Optional<UserSettings> existingSettingsOptional = userSettingsRepository.findByUserId(userId);
        if (existingSettingsOptional.isPresent()) {
            UserSettings existingSettings = existingSettingsOptional.get();
            existingSettings.setGoalAchievementAlert(updatedSettings.isGoalAchievementAlert());
            existingSettings.setApproachingGoalEndAlert(updatedSettings.isApproachingGoalEndAlert());
            existingSettings.setGoalEndAlert(updatedSettings.isGoalEndAlert());
            return userSettingsRepository.save(existingSettings);
        }
        return null;
    }
}

