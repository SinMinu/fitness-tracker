package com.fitness.tracker.controller;

import com.fitness.tracker.dto.GoalProgressDTO;
import com.fitness.tracker.model.Goal;
import com.fitness.tracker.repository.GoalRepository;
import com.fitness.tracker.repository.UserRepository;
import com.fitness.tracker.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GoalRepository goalRepository;

    // 목표 생성 시 currentValue와 targetValue 초기값 설정 확인
    @PostMapping("/user/{userId}")
    public ResponseEntity<Goal> addGoal(@PathVariable Long userId, @RequestBody Goal goal) {
        // currentValue와 targetValue가 올바르게 설정되어 있는지 확인
        if (goal.getTargetValue() <= 0) {
            return ResponseEntity.badRequest().body(null);
        }

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
                .map(goal -> new GoalProgressDTO(goal.getId(), goal.getGoalDescription(), (int) calculateProgress(goal)))
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
        // 목표 진행률을 0%에서 100% 범위로 계산
        double progress = ((double) goal.getCurrentValue() / goal.getTargetValue()) * 100;
        return Math.min(progress, 100); // 100% 이상은 제한
    }

    @PutMapping("/goal/{goalId}/progress")
    public ResponseEntity<Goal> updateGoalProgress(@PathVariable Long goalId, @RequestBody Map<String, Integer> progressData, Principal principal) {
        Goal existingGoal = goalService.findById(goalId);
        if (existingGoal == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // JWT로부터 가져온 사용자와 목표 소유자가 일치하는지 확인
        if (!existingGoal.getUser().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        int newProgress = progressData.get("progress");

        // 진행률을 업데이트
        existingGoal.setCurrentValue(newProgress);

        // 100%가 되었을 때 목표 달성 상태를 true로 설정
        if (newProgress == 100) {
            existingGoal.setAchieved(true);  // 목표 달성 상태를 true로 변경
        } else {
            existingGoal.setAchieved(false); // 목표 달성 상태를 false로 변경
        }

        Goal updatedGoal = goalService.updateGoal(existingGoal.getUser().getId(), goalId, existingGoal);
        return ResponseEntity.ok(updatedGoal);
    }



}