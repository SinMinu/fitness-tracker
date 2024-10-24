package com.fitness.tracker.controller;

import com.fitness.tracker.model.ExerciseRecord;
import com.fitness.tracker.service.ExerciseRecordService;
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

    // 운동 기록 추가
    @PostMapping("/user/{userId}")
    public ResponseEntity<ExerciseRecord> addExerciseRecord(
            @PathVariable Long userId,
            @RequestBody ExerciseRecord exerciseRecord,
            Principal principal) {

        if (!principal.getName().equals(exerciseRecord.getUser().getUsername())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 권한 체크
        }

        ExerciseRecord savedRecord = exerciseRecordService.addExerciseRecord(userId, exerciseRecord);
        if (savedRecord != null) {
            return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 특정 사용자의 운동 기록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExerciseRecord>> getAllExerciseRecordsByUserId(@PathVariable Long userId, Principal principal) {
        // 본인의 기록만 조회할 수 있도록 체크
        if (!principal.getName().equals(exerciseRecordService.findUserById(userId).getUsername())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 권한 체크
        }

        List<ExerciseRecord> records = exerciseRecordService.getAllExerciseRecordsByUserId(userId);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    // 운동 기록 단일 조회
    @GetMapping("/{recordId}")
    public ResponseEntity<ExerciseRecord> getExerciseRecordById(@PathVariable Long recordId, Principal principal) {
        ExerciseRecord record = exerciseRecordService.findById(recordId);

        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 기록이 없을 때
        }

        if (record.getUser() == null || !record.getUser().getUsername().equals(principal.getName())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 권한 체크
        }

        return new ResponseEntity<>(record, HttpStatus.OK);
    }



    // 운동 기록 수정
    @PutMapping("/user/{userId}/record/{recordId}")
    public ResponseEntity<ExerciseRecord> updateExerciseRecord(
            @PathVariable Long userId,
            @PathVariable Long recordId,
            @RequestBody ExerciseRecord updatedRecord) {

        // 기존 기록을 가져오기
        ExerciseRecord existingRecord = exerciseRecordService.findById(recordId);

        if (existingRecord == null || !existingRecord.getUser().getId().equals(userId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // null이 아닌 경우에만 업데이트
        existingRecord.setExerciseName(updatedRecord.getExerciseName() != null ? updatedRecord.getExerciseName() : existingRecord.getExerciseName());
        existingRecord.setExerciseType(updatedRecord.getExerciseType() != null ? updatedRecord.getExerciseType() : existingRecord.getExerciseType());
        existingRecord.setDuration(updatedRecord.getDuration() != null ? updatedRecord.getDuration() : existingRecord.getDuration());
        existingRecord.setCaloriesBurned(updatedRecord.getCaloriesBurned() != null ? updatedRecord.getCaloriesBurned() : existingRecord.getCaloriesBurned());
        existingRecord.setLocation(updatedRecord.getLocation() != null ? updatedRecord.getLocation() : existingRecord.getLocation());
        existingRecord.setEquipment(updatedRecord.getEquipment() != null ? updatedRecord.getEquipment() : existingRecord.getEquipment());
        existingRecord.setIntensity(updatedRecord.getIntensity() != null ? updatedRecord.getIntensity() : existingRecord.getIntensity());
        existingRecord.setNotes(updatedRecord.getNotes() != null ? updatedRecord.getNotes() : existingRecord.getNotes());

        // 기록 업데이트
        ExerciseRecord savedRecord = exerciseRecordService.updateExerciseRecord(userId, recordId, existingRecord);

        return new ResponseEntity<>(savedRecord, HttpStatus.OK);
    }



    // 운동 기록 삭제
    @DeleteMapping("/record/{recordId}")
    public ResponseEntity<Void> deleteExerciseRecord(@PathVariable Long recordId, Principal principal) {
        ExerciseRecord existingRecord = exerciseRecordService.findById(recordId);
        if (existingRecord == null || !existingRecord.getUser().getUsername().equals(principal.getName())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 권한 체크
        }

        exerciseRecordService.deleteExerciseRecord(recordId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
