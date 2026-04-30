package com.eldercare.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 메시지 엔티티
 *
 * 대화 내 개별 메시지 저장
 *
 * TODO: 파일 첨부
 * TODO: 메시지 암호화
 * TODO: 읽음 상태 추적
 */
@Entity
@Table(name = "messages", indexes = {
	@Index(name = "idx_conversation_id", columnList = "conversation_id"),
	@Index(name = "idx_sender_id", columnList = "sender_id"),
	@Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	/**
	 * 메시지 내용
	 */
	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;

	/**
	 * 발신자
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id", nullable = false)
	private Guardian sender;

	/**
	 * 대화
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conversation_id", nullable = false)
	private Conversation conversation;

	/**
	 * 메시지 유형
	 */
	@Column(name = "message_type")
	private String messageType;  // TEXT, IMAGE, FILE, VOICE, EMOTION_ANALYSIS

	/**
	 * 메시지 상태
	 */
	@Column(name = "status")
	private String status;  // SENT, DELIVERED, READ, FAILED

	/**
	 * 읽음 상태
	 */
	@Column(name = "is_read")
	private Boolean isRead;

	@Column(name = "read_at")
	private LocalDateTime readAt;

	/**
	 * 파일 첨부
	 */
	@Column(name = "attachment_url")
	private String attachmentUrl;

	@Column(name = "attachment_type")
	private String attachmentType;  // IMAGE, DOCUMENT, AUDIO, VIDEO

	/**
	 * 감정 분석 (AI)
	 */
	@Column(name = "emotion_score")
	private Double emotionScore;  // -1.0 (부정) ~ 1.0 (긍정)

	@Column(name = "sentiment")
	private String sentiment;  // POSITIVE, NEUTRAL, NEGATIVE

	@Column(name = "emotion_label")
	private String emotionLabel;  // 감정 분류: joy, sadness, anger, etc.

	/**
	 * 메타데이터
	 */
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;  // 소프트 삭제

	@Column(name = "is_encrypted")
	private Boolean isEncrypted;

	@Column(name = "keywords")
	private String keywords;  // JSON: 추출된 키워드

	/**
	 * JPA 라이프사이클 콜백
	 */
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		status = "SENT";
		isRead = false;
		isEncrypted = false;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
