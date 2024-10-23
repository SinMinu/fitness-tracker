package com.fitness.tracker.service;

import com.fitness.tracker.model.Goal;
import com.fitness.tracker.model.User;
import com.fitness.tracker.repository.GoalRepository;
import com.fitness.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    public Goal addGoal(Long userId, Goal goal) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            goal.setUser(user);
            return goalRepository.save(goal);
        }
        return null;
    }

    public List<Goal> getGoalsByUserId(Long userId) {
        return goalRepository.findAll().stream()
                .filter(goal -> goal.getUser().getId().equals(userId))
                .toList();
    }

    public Goal updateGoal(Long userId, Long goalId, Goal updatedGoal) {
        Goal existingGoal = goalRepository.findById(goalId).orElse(null);
        if (existingGoal != null && existingGoal.getUser().getId().equals(userId)) {
            existingGoal.setGoalDescription(updatedGoal.getGoalDescription());
            existingGoal.setTargetValue(updatedGoal.getTargetValue());
            existingGoal.setStartDate(updatedGoal.getStartDate());
            existingGoal.setEndDate(updatedGoal.getEndDate());
            existingGoal.setAchieved(updatedGoal.isAchieved());
            return goalRepository.save(existingGoal);
        }
        return null;
    }

    public void deleteGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }

    public Goal findById(Long goalId) {
        return goalRepository.findById(goalId).orElse(null);
    }


}
