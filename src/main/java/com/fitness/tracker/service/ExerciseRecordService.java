package com.fitness.tracker.service;

import com.fitness.tracker.model.ExerciseRecord;
import com.fitness.tracker.model.User;
import com.fitness.tracker.repository.ExerciseRecordRepository;
import com.fitness.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseRecordService {

    @Autowired
    private ExerciseRecordRepository exerciseRecordRepository;

    @Autowired
    private UserRepository userRepository;

    public ExerciseRecord addExerciseRecord(Long userId, ExerciseRecord exerciseRecord) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            exerciseRecord.setUser(user);
            return exerciseRecordRepository.save(exerciseRecord);
        }
        return null;
    }

    public List<ExerciseRecord> getAllExerciseRecordsByUserId(Long userId) {
        return exerciseRecordRepository.findAll().stream()
                .filter(record -> record.getUser().getId().equals(userId))
                .toList();
    }


    public ExerciseRecord updateExerciseRecord(Long userId, Long recordId, ExerciseRecord updatedRecord) {
        ExerciseRecord existingRecord = exerciseRecordRepository.findById(recordId).orElse(null);
        if (existingRecord != null && existingRecord.getUser().getId().equals(userId)) {
            existingRecord.setExerciseName(updatedRecord.getExerciseName());
            existingRecord.setExerciseType(updatedRecord.getExerciseType());
            existingRecord.setDuration(updatedRecord.getDuration());
            existingRecord.setCaloriesBurned(updatedRecord.getCaloriesBurned());
            existingRecord.setExerciseDate(updatedRecord.getExerciseDate());
            return exerciseRecordRepository.save(existingRecord);
        }
        return null;
    }


    public void deleteExerciseRecord(Long id) {
        exerciseRecordRepository.deleteById(id);
    }

    public ExerciseRecord findById(Long id) {
        return exerciseRecordRepository.findById(id).orElse(null);
    }

}
