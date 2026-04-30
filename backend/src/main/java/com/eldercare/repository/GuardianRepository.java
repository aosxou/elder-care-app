package com.eldercare.repository;

import com.eldercare.entity.Guardian;
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
 * 보호자 Repository
 *
 * 보호자 정보 관리 및 조회
 *
 * TODO: 역할별 조회
 * TODO: 인증 상태별 조회
 * TODO: 성능 최적화
 */
@Repository
public interface GuardianRepository extends JpaRepository<Guardian, String> {

	/**
	 * 이메일로 보호자 조회
	 */
	Optional<Guardian> findByEmail(String email);

	/**
	 * 전화번호로 보호자 조회
	 */
	Optional<Guardian> findByPhone(String phone);

	/**
	 * 역할별 보호자 목록
	 */
	@Query("SELECT g FROM Guardian g WHERE g.role = :role ORDER BY g.createdAt DESC")
	Page<Guardian> findByRole(@Param("role") String role, Pageable pageable);

	/**
	 * 인증 상태별 보호자 목록
	 *
	 * TODO: 인증 대기 중인 보호자 확인
	 */
	@Query("SELECT g FROM Guardian g WHERE g.verificationStatus = :status")
	Page<Guardian> findByVerificationStatus(@Param("status") String status, Pageable pageable);

	/**
	 * 상태별 보호자 목록
	 */
	List<Guardian> findByStatus(String status);

	/**
	 * 이름 또는 이메일로 검색
	 */
	@Query("SELECT g FROM Guardian g WHERE g.name LIKE %:keyword% OR g.email LIKE %:keyword%")
	Page<Guardian> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

	/**
	 * 특정 노인을 관리하는 보호자 목록
	 *
	 * TODO: Elder와의 관계 쿼리
	 */
	@Query(value = "SELECT g.* FROM guardians g " +
			"INNER JOIN guardian_elder ge ON g.id = ge.guardian_id " +
			"WHERE ge.elder_id = :elderId", nativeQuery = true)
	List<Guardian> findGuardiansOfElderly(@Param("elderId") String elderId);

	/**
	 * 특정 역할의 활성 보호자 수
	 */
	@Query("SELECT COUNT(g) FROM Guardian g WHERE g.role = :role AND g.status = 'ACTIVE'")
	Long countActiveByRole(@Param("role") String role);

	/**
	 * 최근 로그인한 보호자
	 */
	@Query("SELECT g FROM Guardian g WHERE g.lastLogin > :since ORDER BY g.lastLogin DESC")
	List<Guardian> findRecentlyLoggedIn(@Param("since") LocalDateTime since);

	/**
	 * 인증되지 않은 보호자
	 */
	@Query("SELECT g FROM Guardian g WHERE g.verificationStatus != 'VERIFIED'")
	Page<Guardian> findUnverified(Pageable pageable);

	/**
	 * 특정 관계의 보호자
	 */
	@Query("SELECT g FROM Guardian g WHERE g.relationship = :relationship")
	Page<Guardian> findByRelationship(@Param("relationship") String relationship, Pageable pageable);

	/**
	 * 건강 경고 알림을 받는 보호자
	 */
	@Query("SELECT g FROM Guardian g WHERE g.healthAlertEnabled = true AND g.status = 'ACTIVE'")
	List<Guardian> findWithHealthAlertEnabled();

	/**
	 * 약물 복용 알림을 받는 보호자
	 */
	@Query("SELECT g FROM Guardian g WHERE g.medicineReminderEnabled = true AND g.status = 'ACTIVE'")
	List<Guardian> findWithMedicineReminderEnabled();

	/**
	 * 긴급 알림을 받는 보호자
	 */
	@Query("SELECT g FROM Guardian g WHERE g.emergencyAlertEnabled = true AND g.status = 'ACTIVE'")
	List<Guardian> findWithEmergencyAlertEnabled();

	/**
	 * 특정 기간 내 가입한 보호자
	 */
	@Query("SELECT g FROM Guardian g WHERE g.createdAt BETWEEN :startDate AND :endDate")
	List<Guardian> findCreatedBetween(
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate
	);

	/**
	 * 이메일 알림을 받는 보호자
	 */
	@Query(value = "SELECT g.* FROM guardians g " +
			"WHERE g.notification_channels LIKE '%EMAIL%'", nativeQuery = true)
	List<Guardian> findWithEmailNotificationEnabled();

	/**
	 * SMS 알림을 받는 보호자
	 */
	@Query(value = "SELECT g.* FROM guardians g " +
			"WHERE g.notification_channels LIKE '%SMS%'", nativeQuery = true)
	List<Guardian> findWithSmsNotificationEnabled();

	/**
	 * 푸시 알림을 받는 보호자
	 */
	@Query(value = "SELECT g.* FROM guardians g " +
			"WHERE g.notification_channels LIKE '%PUSH%'", nativeQuery = true)
	List<Guardian> findWithPushNotificationEnabled();

	/**
	 * 여러 노인을 관리하는 보호자
	 *
	 * TODO: 관리 중인 노인 수
	 */
	@Query(value = "SELECT g.*, COUNT(ge.elder_id) as managed_count FROM guardians g " +
			"LEFT JOIN guardian_elder ge ON g.id = ge.guardian_id " +
			"GROUP BY g.id " +
			"HAVING COUNT(ge.elder_id) > :count", nativeQuery = true)
	List<Guardian> findManagingMultipleElderlies(@Param("count") Integer count);

	/**
	 * 비활성 보호자 (오래된 로그인)
	 */
	@Query("SELECT g FROM Guardian g WHERE g.lastLogin < :threshold AND g.status = 'ACTIVE'")
	List<Guardian> findInactive(@Param("threshold") LocalDateTime threshold);

	/*
	 * TODO: 추가 쿼리 메서드
	 * - 권한별 조회
	 * - 복합 조건 검색
	 * - 통계 쿼리
	 */

	/*
	 * TODO: 성능 최적화
	 * - @EntityGraph 사용
	 * - 배치 처리
	 * - 캐싱
	 */
}
