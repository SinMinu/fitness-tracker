package com.fitness.tracker.service;

import com.fitness.tracker.dto.ExerciseRecommendationDto;
import com.fitness.tracker.model.ExerciseRecord;
import com.fitness.tracker.model.ExerciseStats;
import com.fitness.tracker.model.User;
import com.fitness.tracker.repository.ExerciseRecordRepository;
import com.fitness.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
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

    public ExerciseStats calculateExerciseStats(Long userId) {
        List<ExerciseRecord> records = exerciseRecordRepository.findAllByUserId(userId);

        int totalSessions = records.size();
        double averageDuration = records.stream()
                .mapToDouble(ExerciseRecord::getDuration)
                .average()
                .orElse(0.0);

        String mostFrequentType = records.stream()
                .collect(Collectors.groupingBy(ExerciseRecord::getExerciseType, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No data");

        return new ExerciseStats(totalSessions, averageDuration, mostFrequentType);
    }


    // ExerciseRecordService.java
    public List<ExerciseRecommendationDto> getRecommendationsWithFrequency(Long userId) {
        List<ExerciseRecord> userRecords = exerciseRecordRepository.findAllByUserId(userId);
        Set<String> userExerciseTypes = userRecords.stream()
                .map(ExerciseRecord::getExerciseType)
                .collect(Collectors.toSet());

        Map<String, List<String>> recommendationsMap = new HashMap<>();
        recommendationsMap.put("웨이트 트레이닝", List.of("크로스핏", "킥복싱", "유산소 운동"));
        recommendationsMap.put("유산소 운동", List.of("러닝", "사이클링", "수영"));
        recommendationsMap.put("요가", List.of("필라테스", "스트레칭"));
        recommendationsMap.put("스트레칭", List.of("요가", "필라테스"));
        recommendationsMap.put("러닝", List.of("사이클링", "수영", "웨이트 트레이닝"));

        Set<String> recommendations = new HashSet<>();
        for (String exerciseType : userExerciseTypes) {
            List<String> relatedExercises = recommendationsMap.get(exerciseType);
            if (relatedExercises != null) {
                recommendations.addAll(relatedExercises);
            }
        }
        recommendations.removeAll(userExerciseTypes);

        return recommendations.stream()
                .map(ExerciseRecommendationDto::new)
                .collect(Collectors.toList());
    }

    // 일일, 주간, 월간 통계 계산
    public List<ExerciseStats> calculatePeriodStats(Long userId, String period) {
        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        // 주기 설정
        switch (period) {
            case "weekly":
                startDate = endDate.minusWeeks(1); // 지난 1주일치
                break;
            case "monthly":
                startDate = endDate.minusMonths(1); // 지난 한 달치
                break;
            case "quarterly":
                startDate = endDate.minusMonths(3); // 지난 3개월치
                break;
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }

        // 해당 기간 동안의 기록을 가져오기
        List<ExerciseRecord> records = exerciseRecordRepository.findByUserIdAndExerciseDateBetween(userId, startDate, endDate);

        // 날짜별 그룹화하여 평균 운동 시간과 총 소모 칼로리를 계산
        return records.stream()
                .collect(Collectors.groupingBy(ExerciseRecord::getExerciseDate))
                .entrySet()
                .stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<ExerciseRecord> dailyRecords = entry.getValue();

                    double averageDuration = dailyRecords.stream().mapToDouble(ExerciseRecord::getDuration).average().orElse(0.0);
                    double totalCalories = dailyRecords.stream().mapToDouble(ExerciseRecord::getCaloriesBurned).sum();

                    return new ExerciseStats(averageDuration, totalCalories, date.toString());
                })
                .collect(Collectors.toList());
    }

    public List<String> getPersonalizedRecommendations(Long userId) {
        List<ExerciseRecord> records = exerciseRecordRepository.findAllByUserId(userId);

        if (records.isEmpty()) {
            return List.of("걷기", "조깅", "기본 유산소 운동을 시도해 보세요.");
        }

        // 빈도 높은 운동 유형 찾기
        Map<String, Long> typeFrequency = records.stream()
                .collect(Collectors.groupingBy(ExerciseRecord::getExerciseType, Collectors.counting()));

        System.out.println("typeFrequency: " + typeFrequency); // 빈도 확인용 로그

        String mostFrequentType = typeFrequency.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("유산소 운동");

        System.out.println("mostFrequentType: " + mostFrequentType); // 빈도가 높은 운동 유형 로그

        // 추천 운동 매핑 (더 세분화된 추천)
        Map<String, List<String>> recommendationsMap = Map.of(
                "웨이트 트레이닝", List.of("크로스핏", "킥복싱", "고강도 유산소 운동"),
                "유산소 운동", List.of("사이클링", "수영", "HIIT"),
                "요가", List.of("필라테스", "스트레칭"),
                "러닝", List.of("사이클링", "수영", "웨이트 트레이닝")
        );

        return recommendationsMap.getOrDefault(mostFrequentType, List.of("걷기", "기본 유산소 운동"));
    }

    public ExerciseStats.Period convertToPeriod(String periodStr) {
        switch (periodStr) {
            case "일주일": return ExerciseStats.Period.WEEK;
            case "한달": return ExerciseStats.Period.MONTH;
            case "분기": return ExerciseStats.Period.QUARTER;
            default: throw new IllegalArgumentException("Invalid period: " + periodStr);
        }
    }

}
