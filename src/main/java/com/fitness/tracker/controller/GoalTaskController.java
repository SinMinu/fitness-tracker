package com.fitness.tracker.controller;

import com.fitness.tracker.model.Goal;
import com.fitness.tracker.model.GoalTask;
import com.fitness.tracker.service.GoalService;
import com.fitness.tracker.service.GoalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/goal-tasks")
public class GoalTaskController {
    @Autowired
    private GoalTaskService goalTaskService;
    @Autowired
    private GoalService goalService;

    @GetMapping("/goal/{goalId}")
    public ResponseEntity<List<GoalTask>> getTasksByGoalId(@PathVariable Long goalId) {
        List<GoalTask> tasks = goalTaskService.getTasksByGoalId(goalId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/goal/{goalId}/tasks")
    public ResponseEntity<GoalTask> createGoalTask(@PathVariable Long goalId, @RequestBody GoalTask goalTask, Principal principal) {
        Goal goal = goalService.findById(goalId);
        if (goal == null || !goal.getUser().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        goalTask.setGoal(goal);
        GoalTask savedTask = goalTaskService.createGoalTask(goalTask);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    @PutMapping("/update/{taskId}")
    public ResponseEntity<GoalTask> updateTaskCompletion(@PathVariable Long taskId, @RequestParam boolean isCompleted) {
        GoalTask updatedTask = goalTaskService.updateTaskCompletion(taskId, isCompleted);
        return ResponseEntity.ok(updatedTask);
    }

    @PutMapping("/{taskId}/completion")
    public ResponseEntity<GoalTask> updateTaskStatus(@PathVariable Long taskId, @RequestParam boolean isCompleted, Principal principal) {
        GoalTask task = goalTaskService.findGoalTaskById(taskId);
        if (task == null || !task.getGoal().getUser().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        task.setCompleted(isCompleted);
        GoalTask updatedTask = goalTaskService.updateGoalTask(taskId, task);
        return ResponseEntity.ok(updatedTask);
    }

    @PostMapping("/{goalId}/tasks")
    public ResponseEntity<?> addGoalTask(@PathVariable Long goalId, @RequestBody GoalTask goalTask) {
        try {
            GoalTask savedTask = goalTaskService.addGoalTask(goalId, goalTask);
            return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goal not found");
        }
    }

    // 특정 Goal에 대한 모든 GoalTask 조회
    @GetMapping("/{goalId}/tasks")
    public ResponseEntity<List<GoalTask>> getGoalTasks(@PathVariable Long goalId) {
        Goal goal = goalTaskService.findById(goalId);
        if (goal == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<GoalTask> tasks = goalTaskService.getTasksByGoalId(goalId);
        return ResponseEntity.ok(tasks);
    }

    // 추가된 calculateProgress 메서드는 유지
    private double calculateProgress(Goal goal) {
        if (goal.getTargetValue() == 0) {
            return 0;
        }
        double progress = ((double) goal.getCurrentValue() / goal.getTargetValue()) * 100;
        return Math.min(progress, 100); // 최대 100%로 제한
    }
}
