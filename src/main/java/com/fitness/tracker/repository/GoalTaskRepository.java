package com.fitness.tracker.repository;

import com.fitness.tracker.model.GoalTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalTaskRepository extends JpaRepository<GoalTask, Long> {
    List<GoalTask> findByGoalId(Long goalId);
}
