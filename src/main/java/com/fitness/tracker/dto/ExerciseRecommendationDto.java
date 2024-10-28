package com.fitness.tracker.dto;

public class ExerciseRecommendationDto {
    private String exerciseType;

    // 생성자, getter, setter 추가
    public ExerciseRecommendationDto(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public String getExerciseType() {
        return exerciseType;
    }

}
