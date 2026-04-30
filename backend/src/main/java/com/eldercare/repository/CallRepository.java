package com.eldercare.repository;

import com.eldercare.entity.Call;
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
 * 통화 기록 Repository
 *
 * WebRTC 통화 기록 관리 및 조회
 *
 * TODO: 통화 통계 쿼리
 * TODO: 통화 품질 분석
 * TODO: 성능 최적화
 */
@Repository
public interface CallRepository extends JpaRepository<Call, String> {

	/**
	 * 특정 사용자가 발신한 통화 목록
	 */
	@Query("SELECT c FROM Call c WHERE c.callerId = :callerId ORDER BY c.startedAt DESC")
	Page<Call> findCallsInitiatedBy(@Param("callerId") String callerId, Pageable pageable);

	/**
	 * 특정 사용자가 수신한 통화 목록
	 */
	@Query("SELECT c FROM Call c WHERE c.receiverId = :receiverId ORDER BY c.startedAt DESC")
	Page<Call> findCallsReceivedBy(@Param("receiverId") String receiverId, Pageable pageable);

	/**
	 * 두 사용자 간의 통화 히스토리
	 */
	@Query("SELECT c FROM Call c WHERE " +
			"(c.callerId = :userId1 AND c.receiverId = :userId2) OR " +
			"(c.callerId = :userId2 AND c.receiverId = :userId1) " +
			"ORDER BY c.startedAt DESC")
	Page<Call> findCallsBetween(
			@Param("userId1") String userId1,
			@Param("userId2") String userId2,
			Pageable pageable
	);

	/**
	 * 사용자의 모든 통화 (발신 + 수신)
	 */
	@Query("SELECT c FROM Call c WHERE " +
			"c.callerId = :userId OR c.receiverId = :userId " +
			"ORDER BY c.startedAt DESC")
	Page<Call> findAllCallsByUser(@Param("userId") String userId, Pageable pageable);

	/**
	 * 상태별 통화 조회
	 */
	@Query("SELECT c FROM Call c WHERE c.status = :status ORDER BY c.startedAt DESC")
	Page<Call> findByStatus(@Param("status") String status, Pageable pageable);

	/**
	 * 통화 유형별 조회
	 */
	@Query("SELECT c FROM Call c WHERE c.callType = :type ORDER BY c.startedAt DESC")
	Page<Call> findByCallType(@Param("type") String type, Pageable pageable);

	/**
	 * 특정 기간의 통화 목록
	 */
	@Query("SELECT c FROM Call c WHERE c.startedAt BETWEEN :startDate AND :endDate")
	List<Call> findCallsInDateRange(
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate
	);

	/**
	 * 거절된 통화 목록
	 */
	@Query("SELECT c FROM Call c WHERE c.status = 'REJECTED' OR c.status = 'MISSED'")
	Page<Call> findRejectedOrMissedCalls(Pageable pageable);

	/**
	 * 실패한 통화 목록
	 */
	@Query("SELECT c FROM Call c WHERE c.status = 'FAILED' ORDER BY c.startedAt DESC")
	Page<Call> findFailedCalls(Pageable pageable);

	/**
	 * 특정 사용자의 총 통화 시간
	 */
	@Query("SELECT SUM(c.durationSeconds) FROM Call c WHERE " +
			"(c.callerId = :userId OR c.receiverId = :userId) AND " +
			"c.status = 'ENDED'")
	Long getTotalCallDuration(@Param("userId") String userId);

	/**
	 * 특정 사용자의 총 통화 횟수
	 */
	@Query("SELECT COUNT(c) FROM Call c WHERE " +
			"(c.callerId = :userId OR c.receiverId = :userId) AND " +
			"c.status = 'ENDED'")
	Long getTotalCallCount(@Param("userId") String userId);

	/**
	 * 평균 통화 시간
	 */
	@Query("SELECT AVG(c.durationSeconds) FROM Call c WHERE " +
			"(c.callerId = :userId OR c.receiverId = :userId) AND " +
			"c.status = 'ENDED'")
	Double getAverageCallDuration(@Param("userId") String userId);

	/**
	 * 통화 품질이 나쁜 통화 목록
	 */
	@Query("SELECT c FROM Call c WHERE c.connectionQuality IN ('FAIR', 'POOR')")
	List<Call> findPoorQualityCalls();

	/**
	 * 높은 패킷 손실률의 통화
	 */
	@Query("SELECT c FROM Call c WHERE c.packetLoss > :threshold")
	List<Call> findHighPacketLossCalls(@Param("threshold") Double threshold);

	/**
	 * 높은 지연(latency)의 통화
	 */
	@Query("SELECT c FROM Call c WHERE c.latencyMs > :threshold")
	List<Call> findHighLatencyCalls(@Param("threshold") Integer threshold);

	/**
	 * 녹화된 통화 목록
	 */
	@Query("SELECT c FROM Call c WHERE c.recordingUrl IS NOT NULL")
	Page<Call> findRecordedCalls(Pageable pageable);

	/**
	 * 자막이 있는 통화
	 */
	@Query("SELECT c FROM Call c WHERE c.transcript IS NOT NULL")
	Page<Call> findCallsWithTranscript(Pageable pageable);

	/**
	 * 최근 통화
	 */
	@Query("SELECT c FROM Call c WHERE c.callerId = :userId OR c.receiverId = :userId " +
			"ORDER BY c.startedAt DESC LIMIT 1")
	Optional<Call> findMostRecentCall(@Param("userId") String userId);

	/**
	 * 특정 기간의 통화 통계
	 */
	@Query(value = "SELECT " +
			"COUNT(*) as total_calls, " +
			"SUM(CASE WHEN c.status = 'ENDED' THEN 1 ELSE 0 END) as completed_calls, " +
			"SUM(CASE WHEN c.status = 'MISSED' THEN 1 ELSE 0 END) as missed_calls, " +
			"SUM(CASE WHEN c.status = 'REJECTED' THEN 1 ELSE 0 END) as rejected_calls, " +
			"AVG(c.duration_seconds) as avg_duration " +
			"FROM calls c WHERE c.caller_id = :userId AND " +
			"c.started_at BETWEEN :startDate AND :endDate", nativeQuery = true)
	Object getCallStatistics(
			@Param("userId") String userId,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate
	);

	/**
	 * 장시간 통화
	 */
	@Query("SELECT c FROM Call c WHERE c.durationSeconds > :minDuration ORDER BY c.durationSeconds DESC")
	Page<Call> findLongDurationCalls(@Param("minDuration") Integer minDuration, Pageable pageable);

	/**
	 * 비디오 통화만
	 */
	@Query("SELECT c FROM Call c WHERE c.callType = 'VIDEO'")
	Page<Call> findVideoCalls(Pageable pageable);

	/**
	 * 음성 통화만
	 */
	@Query("SELECT c FROM Call c WHERE c.callType = 'AUDIO'")
	Page<Call> findAudioCalls(Pageable pageable);

	/*
	 * TODO: 추가 쿼리 메서드
	 * - 일일 통화량
	 * - 시간대별 통화 통계
	 * - 사용자별 통화 패턴
	 * - 지역별 통화 품질
	 */

	/*
	 * TODO: 성능 최적화
	 * - 인덱싱
	 * - 파티셔닝 (대용량 데이터)
	 * - 캐싱
	 * - 벌크 연산
	 */
}
