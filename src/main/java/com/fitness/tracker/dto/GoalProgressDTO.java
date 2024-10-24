package com.fitness.tracker.dto;

public class GoalProgressDTO {
    private Long goalId;
    private String goalDescription;
    private int progress;

    public GoalProgressDTO(Long goalId, String goalDescription, int progress) {
        this.goalId = goalId;
        this.goalDescription = goalDescription;
        this.progress = progress;
    }

    // Getters and Setters
    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public String getGoalDescription() {
        return goalDescription;
    }

    public void setGoalDescription(String goalDescription) {
        this.goalDescription = goalDescription;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
