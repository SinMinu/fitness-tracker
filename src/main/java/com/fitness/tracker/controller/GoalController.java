package com.fitness.tracker.controller;

import com.fitness.tracker.model.Goal;
import com.fitness.tracker.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<Goal> addGoal(@PathVariable Long userId, @RequestBody Goal goal) {
        Goal savedGoal = goalService.addGoal(userId, goal);
        if (savedGoal != null) {
            return new ResponseEntity<>(savedGoal, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Goal>> getGoalsByUserId(@PathVariable Long userId) {
        List<Goal> goals = goalService.getGoalsByUserId(userId);
        return new ResponseEntity<>(goals, HttpStatus.OK);
    }

    @PutMapping("/goal/{goalId}")
    public ResponseEntity<Goal> updateGoal(@PathVariable Long goalId, @RequestBody Goal updatedGoal, Principal principal) {
        // 목표 존재 여부 확인 및 소유자 확인
        Goal existingGoal = goalService.findById(goalId);
        if (existingGoal == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (!existingGoal.getUser().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 권한이 없는 경우 403 반환
        }

        // 목표 업데이트
        Goal goal = goalService.updateGoal(existingGoal.getUser().getId(), goalId, updatedGoal);
        return new ResponseEntity<>(goal, HttpStatus.OK);
    }



    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<Goal> getGoalById(@PathVariable Long goalId, Principal principal) {
        Goal goal = goalService.findById(goalId);
        if (goal == null || !goal.getUser().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 권한 검사 실패 시 403 반환
        }
        return ResponseEntity.ok(goal);
    }

}
