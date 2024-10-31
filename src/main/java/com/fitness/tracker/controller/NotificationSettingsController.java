package com.fitness.tracker.controller;

import com.fitness.tracker.model.UserSettings;
import com.fitness.tracker.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification-settings")
public class NotificationSettingsController {

    @Autowired
    private UserSettingsService userSettingsService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserSettings> getUserSettings(@PathVariable Long userId) {
        UserSettings userSettings = userSettingsService.getUserSettings(userId);
        if (userSettings != null) {
            return ResponseEntity.ok(userSettings);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserSettings> updateUserSettings(@PathVariable Long userId, @RequestBody UserSettings updatedSettings) {
        UserSettings userSettings = userSettingsService.updateUserSettings(userId, updatedSettings);
        if (userSettings != null) {
            return ResponseEntity.ok(userSettings);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
