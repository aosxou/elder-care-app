package com.eldercare.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 활동 기록 엔티티
 *
 * 노인의 일상 활동 (걷기, 운동 등) 추적
 *
 * TODO: GPS 위치 정보
 * TODO: 활동 분류
 * TODO: 칼로리 계산
 */
@Entity
@Table(name = "activities", indexes = {
	@Index(name = "idx_elder_id", columnList = "elder_id"),
	@Index(name = "idx_activity_date", columnList = "activity_date"),
	@Index(name = "idx_activity_type", columnList = "activity_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	/**
	 * 활동 기록 대상 노인
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elder_id", nullable = false)
	private Elder elder;

	/**
	 * 활동 유형
	 */
	@Column(name = "activity_type", nullable = false)
	private String activityType;  // WALKING, RUNNING, EXERCISE, REST, etc.

	@Column(name = "activity_name")
	private String activityName;  // 활동 이름

	@Column(name = "description")
	private String description;  // 활동 설명

	/**
	 * 활동 지표
	 */
	@Column(name = "duration_minutes")
	private Integer durationMinutes;  // 활동 지속 시간 (분)

	@Column(name = "distance_km")
	private Double distanceKm;  // 이동 거리 (km)

	@Column(name = "steps")
	private Integer steps;  // 걸음 수

	@Column(name = "calories_burned")
	private Integer caloriesBurned;  // 소모 칼로리

	@Column(name = "average_heart_rate")
	private Integer averageHeartRate;  // 평균 심박수 (BPM)

	@Column(name = "max_heart_rate")
	private Integer maxHeartRate;  // 최대 심박수

	@Column(name = "intensity")
	private String intensity;  // LOW, MEDIUM, HIGH

	/**
	 * 위치 정보
	 */
	@Column(name = "location")
	private String location;  // 위치 이름

	@Column(name = "latitude")
	private Double latitude;  // GPS 위도

	@Column(name = "longitude")
	private Double longitude;  // GPS 경도

	@Column(name = "altitude")
	private Double altitude;  // GPS 고도

	/**
	 * 기상 정보
	 */
	@Column(name = "weather")
	private String weather;  // 날씨 상태

	@Column(name = "temperature")
	private Double temperature;  // 온도 (섭씨)

	@Column(name = "humidity")
	private Integer humidity;  // 습도 (%)

	/**
	 * 활동 기록자 및 기기
	 */
	@Column(name = "recorded_by")
	private String recordedBy;  // WEARABLE, MANUAL, SMARTPHONE, SENSOR

	@Column(name = "device_name")
	private String deviceName;

	@Column(name = "device_id")
	private String deviceId;

	/**
	 * 상태 및 평가
	 */
	@Column(name = "status")
	private String status;  // ONGOING, COMPLETED, PAUSED, CANCELLED

	@Column(name = "effort_level")
	private String effortLevel;  // 주관적 운동 강도 (RPE): 1-10

	@Column(name = "mood")
	private String mood;  // 활동 중 기분: HAPPY, NEUTRAL, TIRED, SAD

	@Column(name = "notes")
	private String notes;  // 추가 메모

	/**
	 * 안전 정보
	 */
	@Column(name = "fall_detected")
	private Boolean fallDetected;  // 낙상 감지

	@Column(name = "safety_alert")
	private String safetyAlert;  // 안전 알림

	/**
	 * 타임스탬프
	 */
	@Column(name = "activity_date", nullable = false)
	private LocalDateTime activityDate;  // 활동 시작 시간

	@Column(name = "end_time")
	private LocalDateTime endTime;  // 활동 종료 시간

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
		if (activityDate == null) {
			activityDate = LocalDateTime.now();
		}
		status = "COMPLETED";
		if (fallDetected == null) {
			fallDetected = false;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
