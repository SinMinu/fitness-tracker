package com.fitness.tracker.controller;

import com.fitness.tracker.dto.GoalProgressDTO;
import com.fitness.tracker.model.Goal;
import com.fitness.tracker.repository.UserRepository;
import com.fitness.tracker.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user/{userId}")
    public ResponseEntity<Goal> addGoal(@PathVariable Long userId, @RequestBody Goal goal) {
        Goal savedGoal = goalService.addGoal(userId, goal);
        if (savedGoal != null) {
            return new ResponseEntity<>(savedGoal, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/progress/user/{userId}")
    public ResponseEntity<List<GoalProgressDTO>> getGoalProgressByUserId(@PathVariable Long userId) {
        List<Goal> goals = goalService.getGoalsByUserId(userId);
        List<GoalProgressDTO> progressList = goals.stream()
                .map(goal -> new GoalProgressDTO(goal.getId(), goal.getGoalDescription(), calculateProgress(goal)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(progressList);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Goal>> getGoalsByUserId(@PathVariable Long userId, Principal principal) {
        if (!principal.getName().equals(userRepository.findById(userId).orElseThrow().getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Goal> goals = goalService.getGoalsByUserId(userId);
        return ResponseEntity.ok(goals);
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

    private double calculateProgress(Goal goal) {
        if (goal.getTargetValue() == 0) {
            return 0;
        }
        double progress = ((double) goal.getCurrentValue() / goal.getTargetValue()) * 100;
        return Math.min(progress, 100); // 최대 100%로 제한
    }


}
