package com.fitness.tracker.repository;

import com.fitness.tracker.model.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {

    List<ExerciseRecord> findByUserIdAndExerciseDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    List<ExerciseRecord> findAllByUserId(Long userId);

    List<ExerciseRecord> findTop5ByUserIdOrderByExerciseDateDesc(Long userId);

    // 운동 빈도 조회 쿼리 추가
    @Query("SELECT e.exerciseType, COUNT(e) AS frequency " +
            "FROM ExerciseRecord e " +
            "WHERE e.user.id = :userId " +
            "GROUP BY e.exerciseType " +
            "ORDER BY frequency DESC")
    List<Object[]> findFrequentExercises(Long userId);
}
