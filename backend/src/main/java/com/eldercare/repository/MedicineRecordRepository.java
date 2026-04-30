package com.eldercare.repository;

import com.eldercare.entity.MedicineRecord;
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
 * 약물 기록 레포지토리
 *
 * 약물 복용 기록 저장, 조회, 분석 기능 제공
 */
@Repository
public interface MedicineRecordRepository extends JpaRepository<MedicineRecord, String> {

	/**
	 * 특정 노인의 약물 기록 조회
	 */
	@Query("SELECT m FROM MedicineRecord m WHERE m.elder = :elder ORDER BY m.scheduledTime DESC")
	List<MedicineRecord> findByElder(@Param("elder") Elder elder);

	/**
	 * 특정 노인의 약물 기록 조회 (페이지네이션)
	 */
	Page<MedicineRecord> findByElder(Elder elder, Pageable pageable);

	/**
	 * 미복용된 약물 조회
	 */
	@Query("SELECT m FROM MedicineRecord m WHERE m.elder = :elder AND m.taken = false AND m.scheduledTime <= :currentTime ORDER BY m.scheduledTime DESC")
	List<MedicineRecord> findMissedMedicines(@Param("elder") Elder elder, @Param("currentTime") LocalDateTime currentTime);

	/**
	 * 특정 기간의 약물 기록
	 */
	@Query("SELECT m FROM MedicineRecord m WHERE m.elder = :elder AND m.scheduledTime BETWEEN :startDate AND :endDate ORDER BY m.scheduledTime DESC")
	List<MedicineRecord> findByDateRange(@Param("elder") Elder elder,
										 @Param("startDate") LocalDateTime startDate,
										 @Param("endDate") LocalDateTime endDate);

	/**
	 * 복용 순응도 계산 (%)
	 */
	@Query("SELECT (COUNT(CASE WHEN m.taken = true THEN 1 END) * 100.0 / COUNT(m)) as adherenceRate FROM MedicineRecord m WHERE m.elder = :elder AND m.scheduledTime BETWEEN :startDate AND :endDate")
	Double getAdherenceRate(@Param("elder") Elder elder,
						   @Param("startDate") LocalDateTime startDate,
						   @Param("endDate") LocalDateTime endDate);

	/**
	 * 특정 약물의 복용 기록
	 */
	@Query("SELECT m FROM MedicineRecord m WHERE m.elder = :elder AND m.medicineName = :medicineName ORDER BY m.scheduledTime DESC")
	List<MedicineRecord> findByMedicineName(@Param("elder") Elder elder, @Param("medicineName") String medicineName);

	/**
	 * 부작용 기록된 약물
	 */
	@Query("SELECT m FROM MedicineRecord m WHERE m.elder = :elder AND m.hasSideEffects = true ORDER BY m.takenAt DESC")
	List<MedicineRecord> findMedicinesWithSideEffects(@Param("elder") Elder elder);

	/**
	 * 특정 부작용의 약물
	 */
	@Query("SELECT m FROM MedicineRecord m WHERE m.elder = :elder AND LOWER(m.sideEffects) LIKE LOWER(CONCAT('%', :sideEffect, '%'))")
	List<MedicineRecord> findBySideEffect(@Param("elder") Elder elder, @Param("sideEffect") String sideEffect);

	/**
	 * 오늘의 복용 일정
	 */
	@Query("SELECT m FROM MedicineRecord m WHERE m.elder = :elder AND CAST(m.scheduledTime as date) = CAST(CURRENT_TIMESTAMP as date) ORDER BY m.scheduledTime")
	List<MedicineRecord> findTodaySchedule(@Param("elder") Elder elder);

	/**
	 * 예정된 복용 약물 (아직 시간이 되지 않은)
	 */
	@Query("SELECT m FROM MedicineRecord m WHERE m.elder = :elder AND m.scheduledTime > :currentTime AND m.taken = false ORDER BY m.scheduledTime")
	List<MedicineRecord> findUpcomingMedicines(@Param("elder") Elder elder, @Param("currentTime") LocalDateTime currentTime);

	/**
	 * 처방 기간 내 활성 약물
	 */
	@Query("SELECT m FROM MedicineRecord m WHERE m.elder = :elder AND m.prescriptionDate <= CURRENT_TIMESTAMP AND (m.prescriptionEndDate IS NULL OR m.prescriptionEndDate >= CURRENT_TIMESTAMP)")
	List<MedicineRecord> findActiveMedicines(@Param("elder") Elder elder);

	/**
	 * 약물 상호작용 위험성 확인 (같은 시간 대 복용 약물)
	 */
	@Query("SELECT m FROM MedicineRecord m WHERE m.elder = :elder AND m.taken = true AND m.takenAt BETWEEN :startTime AND :endTime ORDER BY m.takenAt")
	List<MedicineRecord> findDrugsWithinTimeRange(@Param("elder") Elder elder,
												  @Param("startTime") LocalDateTime startTime,
												  @Param("endTime") LocalDateTime endTime);

	/**
	 * 복용 순응도 좋은 날짜 조회
	 */
	@Query("SELECT CAST(m.scheduledTime as date) as date, COUNT(m) as total, SUM(CASE WHEN m.taken = true THEN 1 ELSE 0 END) as taken FROM MedicineRecord m WHERE m.elder = :elder GROUP BY CAST(m.scheduledTime as date) ORDER BY CAST(m.scheduledTime as date) DESC")
	List<Object[]> getDailyAdherence(@Param("elder") Elder elder);
}
