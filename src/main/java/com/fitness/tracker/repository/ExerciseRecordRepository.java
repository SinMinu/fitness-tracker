package com.fitness.tracker.repository;

import com.fitness.tracker.model.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {
}
