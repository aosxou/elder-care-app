package com.eldercare.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 대화 엔티티
 *
 * 사용자 간, 사용자와 AI 간의 대화 세션 정보
 *
 * TODO: 메시지 수 계산
 * TODO: 감정 분석 결과 저장
 * TODO: 대화 분류
 */
@Entity
@Table(name = "conversations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	/**
	 * 참여자 정보
	 */
	@Column(name = "participant_id", nullable = false)
	private String participantId;  // 첫 번째 사용자

	@Column(name = "target_id")
	private String targetId;  // 두 번째 사용자 또는 AI_ASSISTANT

	@Column(name = "conversation_type")
	private String conversationType;  // USER_TO_USER, USER_TO_AI, GROUP

	/**
	 * 대화 메타데이터
	 */
	@Column(name = "title")
	private String title;

	@Column(name = "description")
	private String description;

	@Column(name = "topic")
	private String topic;  // 대화 주제 (분류용)

	/**
	 * 상태 정보
	 */
	@Column(name = "status")
	private String status;  // ACTIVE, PAUSED, ENDED, ARCHIVED

	@Column(name = "is_archived")
	private Boolean isArchived;

	/**
	 * 메시지 관련
	 */
	@Column(name = "message_count")
	private Integer messageCount;

	@Column(name = "last_message_at")
	private LocalDateTime lastMessageAt;

	@Column(name = "last_message_preview")
	private String lastMessagePreview;  // 마지막 메시지 미리보기

	/**
	 * 시간 정보
	 */
	@Column(name = "started_at", nullable = false)
	private LocalDateTime startedAt;

	@Column(name = "ended_at")
	private LocalDateTime endedAt;

	@Column(name = "duration_minutes")
	private Integer durationMinutes;

	/**
	 * 분석 정보
	 */
	@Column(name = "overall_sentiment")
	private String overallSentiment;  // POSITIVE, NEUTRAL, NEGATIVE

	@Column(name = "sentiment_score")
	private Double sentimentScore;  // -1.0 ~ 1.0

	@Column(name = "key_topics")
	private String keyTopics;  // JSON: 추출된 주요 주제

	@Column(name = "summary")
	private String summary;  // AI 생성 요약

	/**
	 * 설정
	 */
	@Column(name = "is_encrypted")
	private Boolean isEncrypted;  // 암호화 여부

	@Column(name = "is_private")
	private Boolean isPrivate;  // 비공개 여부

	@Column(name = "allow_search")
	private Boolean allowSearch;  // 검색 허용 여부

	/**
	 * 통지 설정
	 */
	@Column(name = "muted")
	private Boolean muted;  // 알림 비활성화

	@Column(name = "pinned")
	private Boolean pinned;  // 상단 고정

	/**
	 * 메시지 (양방향 관계)
	 */
	// TODO: Message와의 일대다 관계 설정
	// @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
	// private List<Message> messages;

	/**
	 * 참여자 정보 (그룹 대화의 경우)
	 */
	@Column(name = "participants")
	private String participants;  // JSON: 모든 참여자 ID 목록

	/**
	 * 메타데이터
	 */
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;  // 소프트 삭제 (논리적 삭제)

	@Column(name = "notes")
	private String notes;

	/**
	 * JPA 라이프사이클 콜백
	 */
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		status = "ACTIVE";
		startedAt = LocalDateTime.now();
		messageCount = 0;
		isArchived = false;
		isEncrypted = false;
		isPrivate = true;
		allowSearch = true;
		muted = false;
		pinned = false;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	/*
	 * TODO: 메서드
	 * - 대화 종료 및 통계 계산
	 * - 대화 요약 생성
	 * - 감정 분석
	 * - 주제 추출
	 * - 아카이브
	 * - 복원
	 */

	/*
	 * TODO: 추가 필드
	 * - 읽지 않은 메시지 수
	 * - 마지막 읽은 메시지 ID
	 * - 참여자별 만족도
	 * - 대화 효과성 점수
	 */
}
