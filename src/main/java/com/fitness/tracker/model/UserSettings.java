package com.fitness.tracker.model;

import jakarta.persistence.*;

@Entity
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean goalAchievementAlert = true;
    private boolean approachingGoalEndAlert = true;
    private boolean goalEndAlert = true;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public boolean isGoalAchievementAlert() { return goalAchievementAlert; }
    public void setGoalAchievementAlert(boolean goalAchievementAlert) { this.goalAchievementAlert = goalAchievementAlert; }

    public boolean isApproachingGoalEndAlert() { return approachingGoalEndAlert; }
    public void setApproachingGoalEndAlert(boolean approachingGoalEndAlert) { this.approachingGoalEndAlert = approachingGoalEndAlert; }

    public boolean isGoalEndAlert() { return goalEndAlert; }
    public void setGoalEndAlert(boolean goalEndAlert) { this.goalEndAlert = goalEndAlert; }
}
