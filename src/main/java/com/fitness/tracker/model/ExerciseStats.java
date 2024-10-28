package com.fitness.tracker.model;

public class ExerciseStats {
    private double averageDuration;
    private double caloriesBurned;
    private String date;

    public enum Period {
        WEEK, MONTH, QUARTER;
    }


    public ExerciseStats(double averageDuration, double caloriesBurned, String date) {
        this.averageDuration = averageDuration;
        this.caloriesBurned = caloriesBurned;
        this.date = date;
    }

    public double getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(double averageDuration) {
        this.averageDuration = averageDuration;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
