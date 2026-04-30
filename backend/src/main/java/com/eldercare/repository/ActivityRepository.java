package com.eldercare.repository;

import com.eldercare.entity.Activity;
import com.eldercare.entity.Elder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 활동 기록 레포지토리
 *
 * 활동 기록 저장, 조회, 분석 기능 제공
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {

	/**
	 * 특정 노인의 활동 기록 조회 (최신순)
	 */
	@Query("SELECT a FROM Activity a WHERE a.elder = :elder ORDER BY a.activityDate DESC")
	List<Activity> findByElder(@Param("elder") Elder elder);

	/**
	 * 특정 노인의 활동 기록 조회 (페이지네이션)
	 */
	Page<Activity> findByElder(Elder elder, Pageable pageable);

	/**
	 * 특정 기간의 활동 기록
	 */
	@Query("SELECT a FROM Activity a WHERE a.elder = :elder AND a.activityDate BETWEEN :startDate AND :endDate ORDER BY a.activityDate DESC")
	List<Activity> findByDateRange(@Param("elder") Elder elder,
								  @Param("startDate") LocalDateTime startDate,
								  @Param("endDate") LocalDateTime endDate);

	/**
	 * 특정 활동 유형의 기록
	 */
	List<Activity> findByElderAndActivityType(Elder elder, String activityType);

	/**
	 * 특정 강도의 활동
	 */
	List<Activity> findByElderAndIntensity(Elder elder, String intensity);

	/**
	 * 일일 총 활동 시간 계산
	 */
	@Query("SELECT SUM(a.durationMinutes) FROM Activity a WHERE a.elder = :elder AND CAST(a.activityDate as date) = CAST(:date as date)")
	Integer getDailyActivityDuration(@Param("elder") Elder elder, @Param("date") LocalDateTime date);

	/**
	 * 일일 총 이동 거리
	 */
	@Query("SELECT COALESCE(SUM(a.distanceKm), 0) FROM Activity a WHERE a.elder = :elder AND CAST(a.activityDate as date) = CAST(:date as date)")
	Double getDailyDistance(@Param("elder") Elder elder, @Param("date") LocalDateTime date);

	/**
	 * 기간별 총 이동 거리
	 */
	@Query("SELECT COALESCE(SUM(a.distanceKm), 0) FROM Activity a WHERE a.elder = :elder AND a.activityDate BETWEEN :startDate AND :endDate")
	Double getTotalDistance(@Param("elder") Elder elder,
						   @Param("startDate") LocalDateTime startDate,
						   @Param("endDate") LocalDateTime endDate);

	/**
	 * 기간별 총 칼로리 소모
	 */
	@Query("SELECT COALESCE(SUM(a.caloriesBurned), 0) FROM Activity a WHERE a.elder = :elder AND a.activityDate BETWEEN :startDate AND :endDate")
	Integer getTotalCaloriesBurned(@Param("elder") Elder elder,
								  @Param("startDate") LocalDateTime startDate,
								  @Param("endDate") LocalDateTime endDate);

	/**
	 * 평균 심박수
	 */
	@Query("SELECT AVG(a.averageHeartRate) FROM Activity a WHERE a.elder = :elder AND a.activityDate BETWEEN :startDate AND :endDate")
	Integer getAverageHeartRate(@Param("elder") Elder elder,
							   @Param("startDate") LocalDateTime startDate,
							   @Param("endDate") LocalDateTime endDate);

	/**
	 * 최대 심박수
	 */
	@Query("SELECT MAX(a.maxHeartRate) FROM Activity a WHERE a.elder = :elder AND a.activityDate BETWEEN :startDate AND :endDate")
	Integer getMaxHeartRate(@Param("elder") Elder elder,
						  @Param("startDate") LocalDateTime startDate,
						  @Param("endDate") LocalDateTime endDate);

	/**
	 * 낙상 감지된 활동
	 */
	@Query("SELECT a FROM Activity a WHERE a.elder = :elder AND a.fallDetected = true ORDER BY a.activityDate DESC")
	List<Activity> findActivitiesWithFallDetection(@Param("elder") Elder elder);

	/**
	 * 특정 위치의 활동
	 */
	@Query("SELECT a FROM Activity a WHERE a.elder = :elder AND a.location LIKE CONCAT('%', :location, '%') ORDER BY a.activityDate DESC")
	List<Activity> findByLocation(@Param("elder") Elder elder, @Param("location") String location);

	/**
	 * 높은 강도 활동 (운동)
	 */
	@Query("SELECT a FROM Activity a WHERE a.elder = :elder AND a.intensity = 'HIGH' ORDER BY a.activityDate DESC")
	List<Activity> findHighIntensityActivities(@Param("elder") Elder elder);

	/**
	 * 일주일 활동 요약
	 */
	@Query("SELECT CAST(a.activityDate as date) as date, a.activityType, COUNT(*) as count, SUM(a.durationMinutes) as totalMinutes, SUM(a.distanceKm) as totalDistance FROM Activity a WHERE a.elder = :elder AND a.activityDate >= :date GROUP BY CAST(a.activityDate as date), a.activityType ORDER BY CAST(a.activityDate as date) DESC")
	List<Object[]> getWeeklyActivitySummary(@Param("elder") Elder elder, @Param("date") LocalDateTime date);

	/**
	 * 활동 유형별 통계
	 */
	@Query("SELECT a.activityType, COUNT(*) as count, SUM(a.durationMinutes) as totalMinutes, SUM(a.distanceKm) as totalDistance, AVG(a.caloriesBurned) as avgCalories FROM Activity a WHERE a.elder = :elder AND a.activityDate BETWEEN :startDate AND :endDate GROUP BY a.activityType")
	List<Object[]> getActivityStatistics(@Param("elder") Elder elder,
										@Param("startDate") LocalDateTime startDate,
										@Param("endDate") LocalDateTime endDate);

	/**
	 * 기분 기반 활동 분석
	 */
	@Query("SELECT a.mood, COUNT(*) as count FROM Activity a WHERE a.elder = :elder AND a.mood IS NOT NULL GROUP BY a.mood")
	List<Object[]> getMoodDistribution(@Param("elder") Elder elder);
}
