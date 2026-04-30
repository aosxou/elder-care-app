package com.eldercare.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 보호자 엔티티
 *
 * 보호자의 기본 정보, 권한, 관리 정보 저장
 *
 * TODO: 다중 노인 관리
 * TODO: 권한 설정
 * TODO: 알림 설정
 * TODO: 접근 로그
 */
@Entity
@Table(name = "guardians")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guardian {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	/**
	 * 기본 정보
	 */
	@Column(nullable = false)
	private String name;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;  // TODO: 암호화 필요

	@Column(nullable = false)
	private String phone;

	@Column(name = "relationship")
	private String relationship;  // 관계 (자식, 배우자, 친구 등)

	/**
	 * 권한 및 역할
	 */
	@Column(name = "role")
	private String role;  // ADMIN, GUARDIAN, CAREGIVER

	@Column(name = "permissions")
	private String permissions;  // TODO: JSON으로 저장

	/**
	 * 관리 대상 노인
	 */
	// TODO: Elder와의 다대다 관계 설정
	// @ManyToMany
	// @JoinTable(
	//     name = "guardian_elder",
	//     joinColumns = @JoinColumn(name = "guardian_id"),
	//     inverseJoinColumns = @JoinColumn(name = "elder_id")
	// )
	// private List<Elder> managedElderlies;

	/**
	 * 상태 정보
	 */
	@Column(name = "status")
	private String status;  // ACTIVE, INACTIVE, SUSPENDED

	@Column(name = "verification_status")
	private String verificationStatus;  // PENDING, VERIFIED, REJECTED

	/**
	 * 알림 설정
	 */
	@Column(name = "health_alert_enabled")
	private Boolean healthAlertEnabled;

	@Column(name = "medicine_reminder_enabled")
	private Boolean medicineReminderEnabled;

	@Column(name = "emergency_alert_enabled")
	private Boolean emergencyAlertEnabled;

	@Column(name = "notification_channels")
	private String notificationChannels;  // JSON: EMAIL, SMS, PUSH

	/**
	 * 주소 정보
	 */
	@Column(name = "address")
	private String address;

	@Column(name = "city")
	private String city;

	@Column(name = "country")
	private String country;

	/**
	 * 타임스탬프
	 */
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	/**
	 * 메타데이터
	 */
	@Column(name = "profile_image_url")
	private String profileImageUrl;

	@Column(name = "notes")
	private String notes;

	@Column(name = "verification_document_url")
	private String verificationDocumentUrl;  // 신분증 사본 등

	/**
	 * JPA 라이프사이클 콜백
	 */
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		status = "ACTIVE";
		verificationStatus = "PENDING";
		healthAlertEnabled = true;
		medicineReminderEnabled = true;
		emergencyAlertEnabled = true;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	/*
	 * TODO: 추가 필드
	 * - 직급/직책
	 * - 소속 기관
	 * - 자격증
	 * - 근무 시간
	 * - 급여 정보 (간병인의 경우)
	 * - 평가 점수
	 */

	/*
	 * TODO: 메서드
	 * - 권한 확인
	 * - 노인 관리 권한 확인
	 * - 알림 설정 조회/변경
	 * - 마지막 로그인 갱신
	 */
}
