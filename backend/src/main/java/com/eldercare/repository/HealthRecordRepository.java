package com.eldercare.repository;

import com.eldercare.entity.HealthRecord;
import com.eldercare.entity.Elder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 건강 기록 레포지토리
 *
 * 건강 지표 저장, 조회, 통계 제공
 */
@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, String> {

	/**
	 * 특정 노인의 건강 기록 조회 (최신순)
	 */
	@Query("SELECT h FROM HealthRecord h WHERE h.elder = :elder ORDER BY h.recordedAt DESC")
	List<HealthRecord> findByElder(@Param("elder") Elder elder);

	/**
	 * 특정 노인의 건강 기록 조회 (페이지네이션)
	 */
	Page<HealthRecord> findByElder(Elder elder, Pageable pageable);

	/**
	 * 특정 기간의 건강 기록
	 */
	@Query("SELECT h FROM HealthRecord h WHERE h.elder = :elder AND h.recordedAt BETWEEN :startDate AND :endDate ORDER BY h.recordedAt DESC")
	List<HealthRecord> findByDateRange(@Param("elder") Elder elder,
									   @Param("startDate") LocalDateTime startDate,
									   @Param("endDate") LocalDateTime endDate);

	/**
	 * 특정 상태의 건강 기록
	 */
	@Query("SELECT h FROM HealthRecord h WHERE h.elder = :elder AND h.status = :status ORDER BY h.recordedAt DESC")
	List<HealthRecord> findByStatus(@Param("elder") Elder elder, @Param("status") String status);

	/**
	 * 경고/위험 상태의 기록
	 */
	@Query("SELECT h FROM HealthRecord h WHERE h.elder = :elder AND h.status IN ('WARNING', 'CRITICAL') ORDER BY h.recordedAt DESC")
	List<HealthRecord> findAbnormalRecords(@Param("elder") Elder elder);

	/**
	 * 가장 최신 건강 기록 조회
	 */
	@Query("SELECT h FROM HealthRecord h WHERE h.elder = :elder ORDER BY h.recordedAt DESC LIMIT 1")
	Optional<HealthRecord> findMostRecentRecord(@Param("elder") Elder elder);

	/**
	 * 혈압 평균 계산
	 */
	@Query("SELECT AVG(h.systolic) as avgSystolic, AVG(h.diastolic) as avgDiastolic FROM HealthRecord h WHERE h.elder = :elder AND h.recordedAt BETWEEN :startDate AND :endDate")
	Object getAverageBloodPressure(@Param("elder") Elder elder,
								   @Param("startDate") LocalDateTime startDate,
								   @Param("endDate") LocalDateTime endDate);

	/**
	 * 혈당 평균 계산
	 */
	@Query("SELECT AVG(h.bloodSugar) FROM HealthRecord h WHERE h.elder = :elder AND h.recordedAt BETWEEN :startDate AND :endDate")
	Double getAverageBloodSugar(@Param("elder") Elder elder,
							   @Param("startDate") LocalDateTime startDate,
							   @Param("endDate") LocalDateTime endDate);

	/**
	 * 맥박 평균 계산
	 */
	@Query("SELECT AVG(h.pulse) FROM HealthRecord h WHERE h.elder = :elder AND h.recordedAt BETWEEN :startDate AND :endDate")
	Integer getAveragePulse(@Param("elder") Elder elder,
						   @Param("startDate") LocalDateTime startDate,
						   @Param("endDate") LocalDateTime endDate);

	/**
	 * 경고가 발생한 기록 조회
	 */
	@Query("SELECT h FROM HealthRecord h WHERE h.elder = :elder AND h.isAlertTriggered = true ORDER BY h.recordedAt DESC")
	List<HealthRecord> findAlertTriggeredRecords(@Param("elder") Elder elder);

	/**
	 * 특정 측정 기기의 기록
	 */
	List<HealthRecord> findByElderAndRecordedBy(Elder elder, String recordedBy);

	/**
	 * 이상 수치 발견
	 */
	@Query("SELECT h FROM HealthRecord h WHERE h.elder = :elder AND (h.systolic > 140 OR h.diastolic > 90 OR h.bloodSugar > 180 OR h.bloodSugar < 70) ORDER BY h.recordedAt DESC")
	List<HealthRecord> findAbnormalReadings(@Param("elder") Elder elder);
}
