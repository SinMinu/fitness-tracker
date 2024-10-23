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

    @PostMapping("/user/{userId}")
    public ResponseEntity<ExerciseRecord> addExerciseRecord(@PathVariable Long userId, @RequestBody ExerciseRecord exerciseRecord) {
        ExerciseRecord savedRecord = exerciseRecordService.addExerciseRecord(userId, exerciseRecord);
        if (savedRecord != null) {
            return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExerciseRecord>> getAllExerciseRecordsByUserId(@PathVariable Long userId) {
        List<ExerciseRecord> records = exerciseRecordService.getAllExerciseRecordsByUserId(userId);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }


    @PutMapping("/user/{userId}/record/{recordId}")
    public ResponseEntity<ExerciseRecord> updateExerciseRecord(@PathVariable Long userId, @PathVariable Long recordId, @RequestBody ExerciseRecord updatedRecord) {
        ExerciseRecord record = exerciseRecordService.updateExerciseRecord(userId, recordId, updatedRecord);
        if (record != null) {
            return new ResponseEntity<>(record, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExerciseRecord(@PathVariable Long id) {
        exerciseRecordService.deleteExerciseRecord(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // src/main/java/com/fitness/tracker/controller/ExerciseRecordController.java
    @GetMapping("/{recordId}")
    public ResponseEntity<ExerciseRecord> getExerciseRecordById(@PathVariable Long recordId, Principal principal) {
        ExerciseRecord record = exerciseRecordService.findById(recordId);
        if (record == null || !record.getUser().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 권한이 없거나 기록이 없는 경우
        }
        return ResponseEntity.ok(record);
    }

}
