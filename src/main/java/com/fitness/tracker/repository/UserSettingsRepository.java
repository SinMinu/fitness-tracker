package com.fitness.tracker.repository;

import com.fitness.tracker.model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUserId(Long userId);  // Optional<UserSettings> 반환
}
