package com.eldercare.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 통화 기록 엔티티
 *
 * WebRTC를 통한 비디오/음성 통화 기록
 *
 * TODO: 통화 품질 메트릭
 * TODO: 통화 내용 저장 (선택적)
 * TODO: 통화 분석
 */
@Entity
@Table(name = "calls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Call {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	/**
	 * 통화자 정보
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "caller_id", nullable = false, updatable = false)
	private Guardian caller;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id", nullable = false, updatable = false)
	private Guardian receiver;

	/**
	 * 피호출 대상자 (노인)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elder_id")
	private Elder elder;

	/**
	 * 통화 유형
	 */
	@Column(name = "call_type")
	private String callType;  // VIDEO, AUDIO, SCREENSHARE

	/**
	 * 통화 상태
	 */
	@Column(name = "status")
	private String status;  // INITIATED, RINGING, ACCEPTED, ENDED, REJECTED, MISSED, FAILED

	/**
	 * 통화 시간 정보
	 */
	@Column(name = "started_at")
	private LocalDateTime startedAt;

	@Column(name = "ended_at")
	private LocalDateTime endedAt;

	@Column(name = "duration_seconds")
	private Integer durationSeconds;

	/**
	 * 통화 품질 지표
	 */
	@Column(name = "connection_quality")
	private String connectionQuality;  // EXCELLENT, GOOD, FAIR, POOR

	@Column(name = "packet_loss")
	private Double packetLoss;  // 패킷 손실율 (%)

	@Column(name = "latency_ms")
	private Integer latencyMs;  // 지연 (밀리초)

	@Column(name = "jitter_ms")
	private Integer jitterMs;  // 떨림 (밀리초)

	@Column(name = "video_bitrate_kbps")
	private Integer videoBitrateKbps;

	@Column(name = "audio_bitrate_kbps")
	private Integer audioBitrateKbps;

	/**
	 * 거절 사유
	 */
	@Column(name = "rejection_reason")
	private String rejectionReason;  // BUSY, DECLINED, TIMEOUT, NETWORK_ERROR 등

	/**
	 * 통화 내용 저장 (선택적)
	 */
	@Column(name = "recording_url")
	private String recordingUrl;  // TODO: 동의 필요

	@Column(name = "transcript")
	private String transcript;  // 통화 기록 (음성 인식)

	/**
	 * 추가 정보
	 */
	@Column(name = "location_caller")
	private String locationCaller;  // 발신자 위치

	@Column(name = "location_receiver")
	private String locationReceiver;  // 수신자 위치

	@Column(name = "device_caller")
	private String deviceCaller;  // 발신자 디바이스

	@Column(name = "device_receiver")
	private String deviceReceiver;  // 수신자 디바이스

	/**
	 * 메타데이터
	 */
	@Column(name = "notes")
	private String notes;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	/**
	 * JPA 라이프사이클 콜백
	 */
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		status = "INITIATED";
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	/*
	 * TODO: 메서드
	 * - 통화 시간 계산
	 * - 통화 품질 평가
	 * - 통화 요약 생성
	 * - 비용 계산
	 */

	/*
	 * TODO: 추가 필드
	 * - 통화 비용
	 * - 참여자 수 (그룹 통화)
	 * - 통화 주제
	 * - 만족도 평가
	 * - 이상 감지 (비정상 길이 등)
	 */
}
