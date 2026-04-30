package com.eldercare.controller;

import com.eldercare.service.WebRTCSignalingService;
import com.eldercare.websocket.WebRTCSignalingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebRTC 시그널링 모니터링 컨트롤러
 *
 * WebRTC 통화 상태, 통계, 온라인 사용자 정보 조회
 */
@Slf4j
@RestController
@RequestMapping("/api/webrtc")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "http://localhost:5173"})
public class WebRTCMonitoringController {

	@Autowired
	private WebRTCSignalingService signalingService;

	/**
	 * 온라인 사용자 목록 조회
	 */
	@GetMapping("/users/online")
	public ResponseEntity<?> getOnlineUsers() {
		log.debug("온라인 사용자 목록 조회");

		List<String> onlineUsers = signalingService.getOnlineUsers();

		Map<String, Object> response = new HashMap<>();
		response.put("onlineUsers", onlineUsers);
		response.put("count", onlineUsers.size());
		response.put("timestamp", System.currentTimeMillis());

		return ResponseEntity.ok(response);
	}

	/**
	 * 특정 사용자의 온라인 상태 확인
	 */
	@GetMapping("/users/{userId}/status")
	public ResponseEntity<?> getUserStatus(@PathVariable String userId) {
		log.debug("사용자 상태 조회: {}", userId);

		boolean isOnline = signalingService.isUserOnline(userId);

		Map<String, Object> response = new HashMap<>();
		response.put("userId", userId);
		response.put("isOnline", isOnline);
		response.put("timestamp", System.currentTimeMillis());

		return ResponseEntity.ok(response);
	}

	/**
	 * 사용자별 통화 통계
	 */
	@GetMapping("/users/{userId}/statistics")
	public ResponseEntity<?> getUserCallStatistics(@PathVariable String userId) {
		log.debug("사용자 통화 통계 조회: {}", userId);

		Map<String, Object> stats = signalingService.getUserCallStatistics(userId);

		return ResponseEntity.ok(stats);
	}

	/**
	 * 활성 통화 목록
	 */
	@GetMapping("/calls/active")
	public ResponseEntity<?> getActiveCalls() {
		log.debug("활성 통화 목록 조회");

		List<WebRTCSignalingHandler.CallInfo> activeCalls = signalingService.getActiveCalls();

		Map<String, Object> response = new HashMap<>();
		response.put("activeCalls", activeCalls);
		response.put("count", activeCalls.size());
		response.put("timestamp", System.currentTimeMillis());

		return ResponseEntity.ok(response);
	}

	/**
	 * 특정 통화 정보 조회
	 */
	@GetMapping("/calls/{callId}")
	public ResponseEntity<?> getCallInfo(@PathVariable String callId) {
		log.debug("통화 정보 조회: {}", callId);

		WebRTCSignalingHandler.CallInfo callInfo = signalingService.getCallInfo(callId);

		if (callInfo == null) {
			return ResponseEntity.notFound().build();
		}

		Map<String, Object> response = new HashMap<>();
		response.put("callId", callInfo.getCallId());
		response.put("from", callInfo.getFrom());
		response.put("to", callInfo.getTo());
		response.put("status", callInfo.getStatus());
		response.put("duration", callInfo.getDurationSeconds());
		response.put("offerTime", callInfo.getOfferTime());
		response.put("answerTime", callInfo.getAnswerTime());
		response.put("endTime", callInfo.getEndTime());
		response.put("timestamp", System.currentTimeMillis());

		return ResponseEntity.ok(response);
	}

	/**
	 * 사용자가 진행 중인 통화 조회
	 */
	@GetMapping("/users/{userId}/active-call")
	public ResponseEntity<?> getUserActiveCall(@PathVariable String userId) {
		log.debug("사용자 활성 통화 조회: {}", userId);

		WebRTCSignalingHandler.CallInfo activeCall = signalingService.findActiveCallByUser(userId);

		if (activeCall == null) {
			return ResponseEntity.ok(new HashMap<String, Object>() {{
				put("hasActiveCall", false);
				put("userId", userId);
			}});
		}

		Map<String, Object> response = new HashMap<>();
		response.put("hasActiveCall", true);
		response.put("callId", activeCall.getCallId());
		response.put("from", activeCall.getFrom());
		response.put("to", activeCall.getTo());
		response.put("status", activeCall.getStatus());
		response.put("duration", activeCall.getDurationSeconds());
		response.put("with", activeCall.getFrom().equals(userId) ? activeCall.getTo() : activeCall.getFrom());

		return ResponseEntity.ok(response);
	}

	/**
	 * 전체 시그널링 통계
	 */
	@GetMapping("/statistics")
	public ResponseEntity<?> getSignalingStatistics() {
		log.debug("전체 시그널링 통계 조회");

		Map<String, Object> stats = signalingService.getSignalingStatistics();

		return ResponseEntity.ok(stats);
	}

	/**
	 * 통화 통계 분석
	 */
	@GetMapping("/calls/statistics")
	public ResponseEntity<?> getCallStatistics() {
		log.debug("통화 통계 분석");

		Map<String, Object> analysis = signalingService.analyzeCallStatistics();

		return ResponseEntity.ok(analysis);
	}

	/**
	 * 시스템 상태 확인
	 */
	@GetMapping("/system/status")
	public ResponseEntity<?> getSystemStatus() {
		log.debug("시스템 상태 확인");

		Map<String, Object> status = signalingService.getSystemStatus();

		return ResponseEntity.ok(status);
	}

	/**
	 * 통화 가능 여부 확인
	 */
	@PostMapping("/calls/can-call")
	public ResponseEntity<?> canCall(@RequestBody Map<String, String> request) {
		String fromUserId = request.get("from");
		String toUserId = request.get("to");

		log.debug("통화 가능 여부 확인: {} -> {}", fromUserId, toUserId);

		// 입력 검증
		if (fromUserId == null || fromUserId.isEmpty() || toUserId == null || toUserId.isEmpty()) {
			return ResponseEntity.badRequest().body(
				new HashMap<String, Object>() {{
					put("error", "Missing from or to user ID");
				}}
			);
		}

		WebRTCSignalingService.CallValidationResult validationResult =
			signalingService.validateCallRequest(fromUserId, toUserId);

		Map<String, Object> response = new HashMap<>();
		response.put("canCall", validationResult.isValid());
		response.put("reason", validationResult.getReason());
		response.put("from", fromUserId);
		response.put("to", toUserId);
		response.put("timestamp", System.currentTimeMillis());

		return ResponseEntity.ok(response);
	}

	/**
	 * 온라인 사용자 수
	 */
	@GetMapping("/users/online/count")
	public ResponseEntity<?> getOnlineUserCount() {
		log.debug("온라인 사용자 수 조회");

		int count = signalingService.getOnlineUserCount();

		Map<String, Object> response = new HashMap<>();
		response.put("onlineCount", count);
		response.put("timestamp", System.currentTimeMillis());

		return ResponseEntity.ok(response);
	}

	/**
	 * 활성 통화 수
	 */
	@GetMapping("/calls/active/count")
	public ResponseEntity<?> getActiveCallCount() {
		log.debug("활성 통화 수 조회");

		int count = signalingService.getActiveCallCount();

		Map<String, Object> response = new HashMap<>();
		response.put("activeCallCount", count);
		response.put("timestamp", System.currentTimeMillis());

		return ResponseEntity.ok(response);
	}

	/**
	 * 종합 대시보드 정보
	 */
	@GetMapping("/dashboard")
	public ResponseEntity<?> getDashboard() {
		log.debug("WebRTC 대시보드 정보 조회");

		Map<String, Object> dashboard = new HashMap<>();

		// 온라인 사용자
		dashboard.put("onlineUsers", signalingService.getOnlineUserCount());

		// 활성 통화
		dashboard.put("activeCalls", signalingService.getActiveCallCount());

		// 시스템 상태
		dashboard.put("systemStatus", signalingService.getSystemStatus());

		// 통화 통계
		dashboard.put("callStatistics", signalingService.analyzeCallStatistics());

		// 타임스탐프
		dashboard.put("timestamp", System.currentTimeMillis());

		return ResponseEntity.ok(dashboard);
	}

	/**
	 * 건강 체크 엔드포인트
	 */
	@GetMapping("/health")
	public ResponseEntity<?> healthCheck() {
		Map<String, Object> health = new HashMap<>();
		health.put("status", "UP");
		health.put("service", "WebRTC Signaling");
		health.put("timestamp", System.currentTimeMillis());

		return ResponseEntity.ok(health);
	}
}
