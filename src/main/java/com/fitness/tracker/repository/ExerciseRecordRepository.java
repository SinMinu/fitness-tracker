package com.fitness.tracker.repository;

import com.fitness.tracker.model.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {
    List<ExerciseRecord> findByUserIdAndExerciseDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<ExerciseRecord> findAllByUserId(Long userId);
    List<ExerciseRecord> findTop5ByUserIdOrderByExerciseDateDesc(Long userId);

}
