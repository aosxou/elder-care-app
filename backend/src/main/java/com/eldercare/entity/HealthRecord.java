package com.eldercare.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 건강 기록 엔티티
 *
 * 노인의 건강 지표 (혈압, 맥박, 혈당 등) 저장
 *
 * TODO: 정상 범위 검증
 * TODO: 이상 감지
 * TODO: 데이터 동기화
 */
@Entity
@Table(name = "health_records", indexes = {
	@Index(name = "idx_elder_id", columnList = "elder_id"),
	@Index(name = "idx_recorded_at", columnList = "recorded_at"),
	@Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	/**
	 * 기록 대상 노인
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elder_id", nullable = false)
	private Elder elder;

	/**
	 * 혈압 정보
	 */
	@Column(name = "systolic")
	private Integer systolic;  // 수축기 혈압 (mmHg)

	@Column(name = "diastolic")
	private Integer diastolic;  // 이완기 혈압 (mmHg)

	/**
	 * 맥박 (heart rate)
	 */
	@Column(name = "pulse")
	private Integer pulse;  // BPM (beats per minute)

	/**
	 * 혈당
	 */
	@Column(name = "blood_sugar")
	private Integer bloodSugar;  // mg/dL

	/**
	 * 체온
	 */
	@Column(name = "temperature")
	private Double temperature;  // 섭씨

	/**
	 * 산소 포화도
	 */
	@Column(name = "spo2")
	private Integer spo2;  // %

	/**
	 * 체질량 지수
	 */
	@Column(name = "bmi")
	private Double bmi;

	/**
	 * 콜레스테롤
	 */
	@Column(name = "cholesterol")
	private Integer cholesterol;  // mg/dL

	/**
	 * 혈당 측정 시간
	 */
	@Column(name = "glucose_measurement_time")
	private String glucoseMeasurementTime;  // 식전, 식후 등

	/**
	 * 기록자 및 장치
	 */
	@Column(name = "recorded_by")
	private String recordedBy;  // WEARABLE, MANUAL, SENSOR

	@Column(name = "device_name")
	private String deviceName;  // 측정 기기명

	/**
	 * 상태 정보
	 */
	@Column(name = "status")
	private String status;  // NORMAL, CAUTION, WARNING, CRITICAL

	@Column(name = "notes")
	private String notes;  // 추가 메모

	@Column(name = "is_alert_triggered")
	private Boolean isAlertTriggered;  // 경고 발생 여부

	@Column(name = "alert_message")
	private String alertMessage;  // 경고 메시지

	/**
	 * 타임스탬프
	 */
	@Column(name = "recorded_at", nullable = false)
	private LocalDateTime recordedAt;  // 측정 시간

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
		if (recordedAt == null) {
			recordedAt = LocalDateTime.now();
		}
		status = "NORMAL";
		isAlertTriggered = false;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
