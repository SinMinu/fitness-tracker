package com.fitness.tracker.service;

import com.fitness.tracker.model.Goal;
import com.fitness.tracker.model.GoalTask;
import com.fitness.tracker.repository.GoalRepository;
import com.fitness.tracker.repository.GoalTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoalTaskService {
    @Autowired
    private GoalTaskRepository goalTaskRepository;
    @Autowired
    private GoalRepository goalRepository;

    public List<GoalTask> getTasksByGoalId(Long goalId) {
        return goalTaskRepository.findByGoalId(goalId);
    }

    public GoalTask createGoalTask(GoalTask goalTask) {
        return goalTaskRepository.save(goalTask);
    }

    public GoalTask updateTaskCompletion(Long taskId, boolean isCompleted) {
        GoalTask goalTask = goalTaskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Invalid task ID"));
        goalTask.setCompleted(isCompleted);
        return goalTaskRepository.save(goalTask);
    }

    public GoalTask addGoalTask(Long goalId, GoalTask goalTask) {
        Optional<Goal> goalOptional = goalRepository.findById(goalId);
        if (goalOptional.isPresent()) {
            goalTask.setGoal(goalOptional.get());
            return goalTaskRepository.save(goalTask);
        }
        throw new IllegalArgumentException("Goal not found");
    }


    public GoalTask updateGoalTask(Long taskId, GoalTask updatedTask) {
        Optional<GoalTask> existingTaskOpt = goalTaskRepository.findById(taskId);
        if (existingTaskOpt.isPresent()) {
            GoalTask existingTask = existingTaskOpt.get();
            existingTask.setTaskName(updatedTask.getTaskName());
            existingTask.setCompleted(updatedTask.isCompleted());
            return goalTaskRepository.save(existingTask);
        }
        return null;
    }

    public void deleteGoalTask(Long taskId) {
        goalTaskRepository.deleteById(taskId);
    }
    public Goal findById(Long goalId) {
        return goalRepository.findById(goalId).orElse(null);
    }

    public GoalTask findGoalTaskById(Long taskId) {
        return goalTaskRepository.findById(taskId).orElse(null);
    }

}
