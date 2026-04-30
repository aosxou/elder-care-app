package com.eldercare.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 약물 복용 기록 엔티티
 *
 * 노인의 약물 복용 이력 저장
 *
 * TODO: 약물 상호작용 검사
 * TODO: 부작용 추적
 * TODO: 복용 순응도 분석
 */
@Entity
@Table(name = "medicine_records", indexes = {
	@Index(name = "idx_elder_id", columnList = "elder_id"),
	@Index(name = "idx_scheduled_time", columnList = "scheduled_time"),
	@Index(name = "idx_taken", columnList = "taken")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	/**
	 * 대상 노인
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elder_id", nullable = false)
	private Elder elder;

	/**
	 * 약물 정보
	 */
	@Column(name = "medicine_name", nullable = false)
	private String medicineName;  // 약물명

	@Column(name = "medicine_code")
	private String medicineCode;  // 의약품 코드

	@Column(name = "dosage")
	private String dosage;  // 용량 (예: 500mg)

	@Column(name = "unit")
	private String unit;  // 단위 (정, mL 등)

	@Column(name = "frequency")
	private String frequency;  // 복용 빈도 (매일, 주 3회 등)

	@Column(name = "route")
	private String route;  // 투약 경로 (경구, 주사, 흡입 등)

	/**
	 * 예약된 복용 시간
	 */
	@Column(name = "scheduled_time", nullable = false)
	private LocalDateTime scheduledTime;

	/**
	 * 실제 복용 기록
	 */
	@Column(name = "taken")
	private Boolean taken;  // 복용 여부

	@Column(name = "taken_at")
	private LocalDateTime takenAt;  // 실제 복용 시간

	@Column(name = "taken_by")
	private String takenBy;  // 복용 자가 입력 또는 간병인 확인

	/**
	 * 미복용 사유
	 */
	@Column(name = "not_taken_reason")
	private String notTakenReason;  // 복용 누락 사유 (장애, 거부, 기타 등)

	/**
	 * 부작용 추적
	 */
	@Column(name = "side_effects")
	private String sideEffects;  // JSON: 부작용 목록

	@Column(name = "has_side_effects")
	private Boolean hasSideEffects;

	@Column(name = "severity")
	private String severity;  // MILD, MODERATE, SEVERE

	/**
	 * 의약품 정보
	 */
	@Column(name = "manufacturer")
	private String manufacturer;

	@Column(name = "expiry_date")
	private String expiryDate;

	@Column(name = "batch_number")
	private String batchNumber;

	@Column(name = "instructions")
	private String instructions;  // 복용 지도사항

	@Column(name = "warnings")
	private String warnings;  // 경고 사항 (JSON)

	/**
	 * 처방 정보
	 */
	@Column(name = "prescribed_by")
	private String prescribedBy;  // 처방의 이름

	@Column(name = "prescription_date")
	private LocalDateTime prescriptionDate;

	@Column(name = "prescription_end_date")
	private LocalDateTime prescriptionEndDate;

	/**
	 * 메모
	 */
	@Column(name = "notes")
	private String notes;

	/**
	 * 타임스탬프
	 */
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
		if (taken == null) {
			taken = false;
		}
		if (hasSideEffects == null) {
			hasSideEffects = false;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
