package com.fitness.tracker.service;

import com.fitness.tracker.model.ExerciseRecord;
import com.fitness.tracker.model.User;
import com.fitness.tracker.repository.ExerciseRecordRepository;
import com.fitness.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExerciseRecordService {

    @Autowired
    private ExerciseRecordRepository exerciseRecordRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    public ExerciseRecord addExerciseRecord(Long userId, ExerciseRecord exerciseRecord) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            exerciseRecord.setUser(user); // 사용자 설정
            return exerciseRecordRepository.save(exerciseRecord);
        }
        return null;
    }

    public List<ExerciseRecord> getAllExerciseRecordsByUserId(Long userId) {
        return exerciseRecordRepository.findAll().stream()
                .filter(record -> record.getUser().getId().equals(userId))
                .toList();
    }


    public ExerciseRecord updateExerciseRecord(Long userId, Long recordId, ExerciseRecord updatedRecord) {
        ExerciseRecord existingRecord = exerciseRecordRepository.findById(recordId).orElse(null);
        if (existingRecord != null && existingRecord.getUser().getId().equals(userId)) {
            existingRecord.setExerciseName(updatedRecord.getExerciseName());
            existingRecord.setExerciseType(updatedRecord.getExerciseType());
            existingRecord.setDuration(updatedRecord.getDuration());
            existingRecord.setCaloriesBurned(updatedRecord.getCaloriesBurned());
            existingRecord.setExerciseDate(String.valueOf(updatedRecord.getExerciseDate()));
            return exerciseRecordRepository.save(existingRecord);
        }
        return null;
    }


    public void deleteExerciseRecord(Long id) {
        exerciseRecordRepository.deleteById(id);
    }

    public ExerciseRecord findById(Long id) {
        return exerciseRecordRepository.findById(id).orElse(null);
    }

    public User findUserById(Long userId) {
        return userService.findUserById(userId);
    }

    public List<ExerciseRecord> getExerciseRecordsByDateRange(Long userId, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return exerciseRecordRepository.findByUserIdAndExerciseDateBetween(userId, start, end);
    }

    // 추천 운동 목록 생성 로직
    public List<String> getRecommendations(Long userId) {
        List<ExerciseRecord> userRecords = exerciseRecordRepository.findAllByUserId(userId);
        Set<String> userExerciseTypes = userRecords.stream()
                .map(ExerciseRecord::getExerciseType)
                .collect(Collectors.toSet());

        // 기본 운동 추천 리스트
        Map<String, List<String>> recommendationsMap = new HashMap<>();
        recommendationsMap.put("웨이트 트레이닝", List.of("크로스핏", "킥복싱", "유산소 운동"));
        recommendationsMap.put("유산소 운동", List.of("러닝", "사이클링", "수영"));
        recommendationsMap.put("요가", List.of("필라테스", "스트레칭"));
        recommendationsMap.put("스트레칭", List.of("요가", "필라테스"));
        recommendationsMap.put("러닝", List.of("사이클링", "수영", "웨이트 트레이닝"));

        // 추천 운동을 저장할 리스트
        Set<String> recommendations = new HashSet<>();

        // 사용자의 운동 종류에 따른 추천 운동 추가
        for (String type : userExerciseTypes) {
            List<String> relatedExercises = recommendationsMap.get(type);
            if (relatedExercises != null) {
                recommendations.addAll(relatedExercises);
            }
        }

        return new ArrayList<>(recommendations); // 중복을 제거한 후 리스트로 반환
    }

    // 총 운동 시간 계산
    private int getTotalDuration(List<ExerciseRecord> exerciseRecords) {
        return exerciseRecords.stream().mapToInt(ExerciseRecord::getDuration).sum();
    }

}
