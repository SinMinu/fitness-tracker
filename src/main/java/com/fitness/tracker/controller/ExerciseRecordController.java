package com.fitness.tracker.controller;

import com.fitness.tracker.dto.ExerciseRecommendationDto;
import com.fitness.tracker.model.ExerciseRecord;
import com.fitness.tracker.model.ExerciseStats;
import com.fitness.tracker.model.User;
import com.fitness.tracker.service.ExerciseRecordService;
import com.fitness.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/exercise-records")
public class ExerciseRecordController {

    @Autowired
    private ExerciseRecordService exerciseRecordService;
    @Autowired
    private UserService userService;

    // 운동 기록 추가
    @PostMapping("/user/{userId}")
    public ResponseEntity<ExerciseRecord> addExerciseRecord(
            @PathVariable Long userId,
            @RequestBody ExerciseRecord exerciseRecord,
            Principal principal) {

        User currentUser = userService.findUserByUsername(principal.getName());
        if (currentUser == null || !currentUser.getId().equals(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        exerciseRecord.setUser(currentUser);
        ExerciseRecord savedRecord = exerciseRecordService.addExerciseRecord(userId, exerciseRecord);
        return savedRecord != null ? new ResponseEntity<>(savedRecord, HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 특정 사용자의 운동 기록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExerciseRecord>> getAllExerciseRecordsByUserId(@PathVariable Long userId, Principal principal) {
        if (!principal.getName().equals(exerciseRecordService.findUserById(userId).getUsername())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<ExerciseRecord> records = exerciseRecordService.getAllExerciseRecordsByUserId(userId);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    // 운동 기록 단일 조회
    @GetMapping("/{recordId}")
    public ResponseEntity<ExerciseRecord> getExerciseRecordById(@PathVariable Long recordId, Principal principal) {
        ExerciseRecord record = exerciseRecordService.findById(recordId);

        if (record == null || !record.getUser().getUsername().equals(principal.getName())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(record, HttpStatus.OK);
    }

    // 운동 기록 수정
    @PutMapping("/user/{userId}/record/{recordId}")
    public ResponseEntity<ExerciseRecord> updateExerciseRecord(
            @PathVariable Long userId,
            @PathVariable Long recordId,
            @RequestBody ExerciseRecord updatedRecord) {

        ExerciseRecord existingRecord = exerciseRecordService.findById(recordId);

        if (existingRecord == null || !existingRecord.getUser().getId().equals(userId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        existingRecord.setExerciseName(updatedRecord.getExerciseName() != null ? updatedRecord.getExerciseName() : existingRecord.getExerciseName());
        existingRecord.setExerciseType(updatedRecord.getExerciseType() != null ? updatedRecord.getExerciseType() : existingRecord.getExerciseType());
        existingRecord.setDuration(updatedRecord.getDuration() != null ? updatedRecord.getDuration() : existingRecord.getDuration());
        existingRecord.setCaloriesBurned(updatedRecord.getCaloriesBurned() != null ? updatedRecord.getCaloriesBurned() : existingRecord.getCaloriesBurned());
        existingRecord.setLocation(updatedRecord.getLocation() != null ? updatedRecord.getLocation() : existingRecord.getLocation());
        existingRecord.setEquipment(updatedRecord.getEquipment() != null ? updatedRecord.getEquipment() : existingRecord.getEquipment());
        existingRecord.setIntensity(updatedRecord.getIntensity() != null ? updatedRecord.getIntensity() : existingRecord.getIntensity());
        existingRecord.setNotes(updatedRecord.getNotes() != null ? updatedRecord.getNotes() : existingRecord.getNotes());

        ExerciseRecord savedRecord = exerciseRecordService.updateExerciseRecord(userId, recordId, existingRecord);
        return new ResponseEntity<>(savedRecord, HttpStatus.OK);
    }

    // 운동 기록 삭제
    @DeleteMapping("/record/{recordId}")
    public ResponseEntity<Void> deleteExerciseRecord(@PathVariable Long recordId, Principal principal) {
        ExerciseRecord existingRecord = exerciseRecordService.findById(recordId);
        if (existingRecord == null || !existingRecord.getUser().getUsername().equals(principal.getName())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        exerciseRecordService.deleteExerciseRecord(recordId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 날짜 범위에 따른 운동 기록 분석
    @GetMapping("/user/{userId}/analysis")
    public ResponseEntity<List<ExerciseRecord>> getExerciseRecordsByDateRange(
            @PathVariable Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        List<ExerciseRecord> records = exerciseRecordService.getExerciseRecordsByDateRange(userId, startDate, endDate);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    // 운동 추천
    @GetMapping("/user/{userId}/recommendations")
    public ResponseEntity<List<ExerciseRecommendationDto>> getRecommendations(@PathVariable Long userId, Principal principal) {
        User currentUser = userService.findUserByUsername(principal.getName());
        if (currentUser == null || !currentUser.getId().equals(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<ExerciseRecommendationDto> recommendations = exerciseRecordService.getRecommendationsWithFrequency(userId);
        return new ResponseEntity<>(recommendations, HttpStatus.OK);
    }


    // 사용자 운동 통계 제공
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<ExerciseStats> getUserExerciseStats(@PathVariable Long userId) {
        ExerciseStats stats = exerciseRecordService.calculateExerciseStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/user/{userId}/stats/{period}")
    public ResponseEntity<List<ExerciseStats>> getPeriodExerciseStats(@PathVariable Long userId, @PathVariable String period) {
        List<ExerciseStats> stats = exerciseRecordService.calculatePeriodStats(userId, period);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/user/{userId}/personalized-recommendations")
    public ResponseEntity<List<String>> getPersonalizedRecommendations(@PathVariable Long userId, Principal principal) {
        User currentUser = userService.findUserByUsername(principal.getName());
        if (currentUser == null || !currentUser.getId().equals(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<String> recommendations = exerciseRecordService.getPersonalizedRecommendations(userId);
        return new ResponseEntity<>(recommendations, HttpStatus.OK);
    }
}
