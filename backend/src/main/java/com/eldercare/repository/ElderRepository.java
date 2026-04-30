package com.eldercare.repository;

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
 * 노인 사용자 Repository
 *
 * JPA Repository를 확장하여 데이터베이스 접근
 *
 * TODO: 커스텀 쿼리 작성
 * TODO: 성능 최적화 (인덱싱)
 * TODO: 캐싱 설정
 */
@Repository
public interface ElderRepository extends JpaRepository<Elder, String> {

	/**
	 * 이메일로 노인 조회
	 */
	Optional<Elder> findByEmail(String email);

	/**
	 * 전화번호로 노인 조회
	 */
	Optional<Elder> findByPhone(String phone);

	/**
	 * 상태별 노인 목록 조회
	 *
	 * TODO: 페이지네이션 추가
	 */
	List<Elder> findByStatus(String status);

	/**
	 * 건강 상태별 노인 목록 조회
	 */
	@Query("SELECT e FROM Elder e WHERE e.healthStatus = :healthStatus")
	Page<Elder> findByHealthStatus(@Param("healthStatus") String healthStatus, Pageable pageable);

	/**
	 * 이름으로 노인 검색
	 *
	 * TODO: 부분 매칭 검색
	 */
	@Query("SELECT e FROM Elder e WHERE e.name LIKE %:keyword% OR e.email LIKE %:keyword%")
	Page<Elder> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

	/**
	 * 특정 보호자가 관리하는 노인 목록
	 *
	 * TODO: Guardian과의 관계 쿼리 필요
	 */
	@Query(value = "SELECT e.* FROM elders e " +
			"INNER JOIN guardian_elder ge ON e.id = ge.elder_id " +
			"WHERE ge.guardian_id = :guardianId", nativeQuery = true)
	Page<Elder> findManagedByGuardian(@Param("guardianId") String guardianId, Pageable pageable);

	/**
	 * 최근 로그인한 노인 목록
	 */
	@Query("SELECT e FROM Elder e WHERE e.lastLogin > :since ORDER BY e.lastLogin DESC")
	List<Elder> findRecentlyLoggedIn(@Param("since") LocalDateTime since);

	/**
	 * 활성 노인 수
	 */
	@Query("SELECT COUNT(e) FROM Elder e WHERE e.status = 'ACTIVE'")
	Long countActiveElderlies();

	/**
	 * 건강 경고가 있는 노인 목록
	 */
	@Query("SELECT e FROM Elder e WHERE e.healthStatus IN ('CAUTION', 'WARNING', 'CRITICAL')")
	List<Elder> findWithHealthAlerts();

	/**
	 * 약물 복용 미리스트 노인 목록
	 *
	 * TODO: Medicine 엔티티와의 관계 쿼리
	 */
	@Query(value = "SELECT e.* FROM elders e " +
			"INNER JOIN medicines m ON e.id = m.elder_id " +
			"WHERE m.status = 'PENDING' AND m.scheduled_time <= NOW()", nativeQuery = true)
	List<Elder> findWithPendingMedicines();

	/**
	 * 특정 기간 내 생성된 노인
	 */
	@Query("SELECT e FROM Elder e WHERE e.createdAt BETWEEN :startDate AND :endDate")
	List<Elder> findCreatedBetween(
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate
	);

	/**
	 * 인증 대기 중인 보호자가 있는 노인
	 *
	 * TODO: Guardian과의 관계 쿼리
	 */
	@Query(value = "SELECT DISTINCT e.* FROM elders e " +
			"INNER JOIN guardian_elder ge ON e.id = ge.elder_id " +
			"INNER JOIN guardians g ON ge.guardian_id = g.id " +
			"WHERE g.verification_status = 'PENDING'", nativeQuery = true)
	List<Elder> findWithPendingGuardianVerification();

	/**
	 * 비활성 노인 (오래된 로그인)
	 *
	 * TODO: 활동이 없는 사용자 찾기
	 */
	@Query("SELECT e FROM Elder e WHERE e.lastLogin < :threshold")
	List<Elder> findInactive(@Param("threshold") LocalDateTime threshold);

	/**
	 * 알레르기 정보로 검색
	 *
	 * TODO: 데이터베이스에서 JSON 검색
	 */
	@Query(value = "SELECT e.* FROM elders e " +
			"WHERE e.allergies LIKE %:allergen%", nativeQuery = true)
	List<Elder> findByAllergy(@Param("allergen") String allergen);

	/**
	 * 특정 만성질환이 있는 노인
	 */
	@Query(value = "SELECT e.* FROM elders e " +
			"WHERE e.chronic_diseases LIKE %:disease%", nativeQuery = true)
	List<Elder> findByChronicDisease(@Param("disease") String disease);

	/**
	 * 나이 범위로 검색
	 *
	 * TODO: 생일로부터 나이 계산
	 */
	@Query(value = "SELECT e.* FROM elders e " +
			"WHERE YEAR(CURDATE()) - YEAR(e.date_of_birth) BETWEEN :minAge AND :maxAge",
			nativeQuery = true)
	List<Elder> findByAgeRange(
			@Param("minAge") Integer minAge,
			@Param("maxAge") Integer maxAge
	);

	/*
	 * TODO: 추가 쿼리 메서드
	 * - 정렬된 조회
	 * - 복합 조건 검색
	 * - 통계 쿼리
	 * - 벌크 업데이트
	 */

	/*
	 * TODO: 성능 최적화
	 * - @EntityGraph 사용 (Lazy Loading 최적화)
	 * - 인덱스 설정
	 * - 쿼리 최적화
	 */
}
