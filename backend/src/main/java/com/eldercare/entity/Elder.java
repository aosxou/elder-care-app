package com.eldercare.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 노인 사용자 엔티티
 *
 * 노인의 기본 정보, 건강 정보, 관계 정보 저장
 *
 * TODO: 건강 데이터 추가 (혈압, 혈당 등)
 * TODO: 약물 정보 매핑
 * TODO: 건강 이력 저장
 * TODO: 알레르기 정보
 */
@Entity
@Table(name = "elders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Elder {

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

	@Column(name = "date_of_birth")
	private String dateOfBirth;

	@Column(name = "gender")
	private String gender;  // M/F

	@Column(name = "address")
	private String address;

	/**
	 * 건강 관련 정보
	 */
	@Column(name = "blood_type")
	private String bloodType;

	@Column(name = "allergies")
	private String allergies;  // TODO: JSON으로 저장

	@Column(name = "chronic_diseases")
	private String chronicDiseases;  // TODO: JSON으로 저장

	@Column(name = "current_medications")
	private String currentMedications;  // TODO: JSON으로 저장

	/**
	 * 상태 정보
	 */
	@Column(name = "status")
	private String status;  // ACTIVE, INACTIVE, CRITICAL

	@Column(name = "health_status")
	private String healthStatus;  // GOOD, CAUTION, WARNING, CRITICAL

	/**
	 * 보호자 정보 (다대다 관계)
	 */
	@ManyToMany(mappedBy = "managedElderlies", fetch = FetchType.LAZY)
	private List<Guardian> guardians;

	/**
	 * 통화 기록
	 */
	@OneToMany(mappedBy = "elder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Call> calls;

	/**
	 * 건강 기록
	 */
	@OneToMany(mappedBy = "elder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<HealthRecord> healthRecords;

	/**
	 * 약물 기록
	 */
	@OneToMany(mappedBy = "elder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<MedicineRecord> medicineRecords;

	/**
	 * 활동 기록
	 */
	@OneToMany(mappedBy = "elder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Activity> activities;

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

	@Column(name = "emergency_contact")
	private String emergencyContact;  // TODO: JSON으로 저장

	@Column(name = "notes")
	private String notes;  // 특이사항

	/**
	 * JPA 라이프사이클 콜백
	 */
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		status = "ACTIVE";
		healthStatus = "GOOD";
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	/*
	 * TODO: 추가 필드
	 * - 음성 선호도
	 * - 언어 설정
	 * - 알림 설정
	 * - 개인화 설정
	 * - 질병 이력
	 * - 가족력
	 */

	/*
	 * TODO: 메서드
	 * - 건강 상태 업데이트
	 * - 마지막 로그인 갱신
	 * - 프로필 완성도 계산
	 */
}
