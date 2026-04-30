package com.eldercare.service;

import com.eldercare.websocket.WebRTCSignalingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebRTC 시그널링 서비스
 *
 * WebRTC 통화 상태 모니터링, 통계, 분석 기능 제공
 */
@Slf4j
@Service
public class WebRTCSignalingService {

	@Autowired
	private WebRTCSignalingHandler signalingHandler;

	/**
	 * 온라인 클라이언트 목록 조회
	 */
	public List<String> getOnlineUsers() {
		return signalingHandler.getOnlineUsers();
	}

	/**
	 * 특정 사용자의 온라인 상태 확인
	 */
	public boolean isUserOnline(String userId) {
		return signalingHandler.isClientOnline(userId);
	}

	/**
	 * 온라인 사용자 수
	 */
	public int getOnlineUserCount() {
		return signalingHandler.getConnectedClientCount();
	}

	/**
	 * 활성 통화 목록
	 */
	public List<WebRTCSignalingHandler.CallInfo> getActiveCalls() {
		return signalingHandler.getAllActiveCalls();
	}

	/**
	 * 특정 통화 정보 조회
	 */
	public WebRTCSignalingHandler.CallInfo getCallInfo(String callId) {
		return signalingHandler.getCallInfo(callId);
	}

	/**
	 * 활성 통화 수
	 */
	public int getActiveCallCount() {
		return signalingHandler.getAllActiveCalls().size();
	}

	/**
	 * 특정 사용자가 진행 중인 통화 찾기
	 */
	public WebRTCSignalingHandler.CallInfo findActiveCallByUser(String userId) {
		return signalingHandler.getAllActiveCalls().stream()
			.filter(call -> call.getFrom().equals(userId) || call.getTo().equals(userId))
			.filter(call -> "offer".equals(call.getStatus()) || "answered".equals(call.getStatus()))
			.findFirst()
			.orElse(null);
	}

	/**
	 * WebRTC 시그널링 통계
	 */
	public Map<String, Object> getSignalingStatistics() {
		Map<String, Object> stats = new HashMap<>();

		// 온라인 사용자
		List<String> onlineUsers = getOnlineUsers();
		stats.put("onlineUsers", onlineUsers.size());
		stats.put("userList", onlineUsers);

		// 활성 통화
		List<WebRTCSignalingHandler.CallInfo> activeCalls = getActiveCalls();
		stats.put("activeCalls", activeCalls.size());

		// 통화별 통계
		if (!activeCalls.isEmpty()) {
			Map<String, Object> callDetails = new HashMap<>();
			for (WebRTCSignalingHandler.CallInfo call : activeCalls) {
				Map<String, Object> callInfo = new HashMap<>();
				callInfo.put("callId", call.getCallId());
				callInfo.put("from", call.getFrom());
				callInfo.put("to", call.getTo());
				callInfo.put("status", call.getStatus());
				callInfo.put("duration", call.getDurationSeconds());
				callInfo.put("offerTime", call.getOfferTime());
				callInfo.put("answerTime", call.getAnswerTime());

				callDetails.put(call.getCallId(), callInfo);
			}
			stats.put("callDetails", callDetails);
		}

		// 타임스탬프
		stats.put("timestamp", System.currentTimeMillis());

		return stats;
	}

	/**
	 * 사용자별 통화 통계
	 */
	public Map<String, Object> getUserCallStatistics(String userId) {
		Map<String, Object> userStats = new HashMap<>();

		userStats.put("userId", userId);
		userStats.put("isOnline", isUserOnline(userId));

		// 현재 진행 중인 통화
		WebRTCSignalingHandler.CallInfo activeCall = findActiveCallByUser(userId);
		if (activeCall != null) {
			Map<String, Object> callInfo = new HashMap<>();
			callInfo.put("callId", activeCall.getCallId());
			callInfo.put("with", activeCall.getFrom().equals(userId) ? activeCall.getTo() : activeCall.getFrom());
			callInfo.put("status", activeCall.getStatus());
			callInfo.put("duration", activeCall.getDurationSeconds());
			userStats.put("activeCall", callInfo);
		}

		// 클라이언트 정보
		WebRTCSignalingHandler.ClientInfo clientInfo = signalingHandler.getClientInfo(userId);
		if (clientInfo != null) {
			Map<String, Object> info = new HashMap<>();
			info.put("userId", clientInfo.getUserId());
			info.put("ipAddress", clientInfo.getIpAddress());
			info.put("connectedAt", clientInfo.getConnectedAt());
			info.put("lastActivity", clientInfo.getLastActivity());
			userStats.put("clientInfo", info);
		}

		userStats.put("timestamp", System.currentTimeMillis());

		return userStats;
	}

	/**
	 * 시스템 상태 체크
	 */
	public Map<String, Object> getSystemStatus() {
		Map<String, Object> status = new HashMap<>();

		status.put("timestamp", System.currentTimeMillis());
		status.put("connectedClients", getOnlineUserCount());
		status.put("activeCalls", getActiveCallCount());
		status.put("status", "healthy");

		// 경고 조건 확인
		if (getOnlineUserCount() > 1000) {
			status.put("status", "warning");
			status.put("warning", "High number of connected clients");
		}

		if (getActiveCallCount() > 500) {
			status.put("status", "warning");
			status.put("warning", "High number of active calls");
		}

		return status;
	}

	/**
	 * 사용자 그룹 통화 가능 여부 확인
	 */
	public boolean canCallUser(String fromUserId, String toUserId) {
		// 대상 사용자가 온라인인지 확인
		if (!isUserOnline(toUserId)) {
			return false;
		}

		// 대상 사용자가 이미 다른 통화 중인지 확인
		WebRTCSignalingHandler.CallInfo activeCall = findActiveCallByUser(toUserId);
		if (activeCall != null) {
			return false;
		}

		return true;
	}

	/**
	 * 통화 요청 상태 검증
	 */
	public CallValidationResult validateCallRequest(String fromUserId, String toUserId) {
		CallValidationResult result = new CallValidationResult();

		// 발신자가 온라인인지 확인
		if (!isUserOnline(fromUserId)) {
			result.setValid(false);
			result.setReason("Caller is not online");
			return result;
		}

		// 수신자가 온라인인지 확인
		if (!isUserOnline(toUserId)) {
			result.setValid(false);
			result.setReason("Receiver is not online");
			return result;
		}

		// 발신자가 이미 다른 통화 중인지 확인
		WebRTCSignalingHandler.CallInfo callerActiveCall = findActiveCallByUser(fromUserId);
		if (callerActiveCall != null) {
			result.setValid(false);
			result.setReason("Caller is already in a call");
			return result;
		}

		// 수신자가 이미 다른 통화 중인지 확인
		WebRTCSignalingHandler.CallInfo receiverActiveCall = findActiveCallByUser(toUserId);
		if (receiverActiveCall != null) {
			result.setValid(false);
			result.setReason("Receiver is already in a call");
			return result;
		}

		result.setValid(true);
		result.setReason("Call request is valid");

		return result;
	}

	/**
	 * 통화 통계 분석
	 */
	public Map<String, Object> analyzeCallStatistics() {
		Map<String, Object> analysis = new HashMap<>();

		List<WebRTCSignalingHandler.CallInfo> allCalls = signalingHandler.getAllActiveCalls();

		if (allCalls.isEmpty()) {
			analysis.put("totalCalls", 0);
			return analysis;
		}

		analysis.put("totalCalls", allCalls.size());

		// 상태별 통화 수
		long offerCount = allCalls.stream().filter(c -> "offer".equals(c.getStatus())).count();
		long answeredCount = allCalls.stream().filter(c -> "answered".equals(c.getStatus())).count();

		analysis.put("pendingOffers", offerCount);
		analysis.put("activeConnections", answeredCount);

		// 평균 통화 지속시간
		double avgDuration = allCalls.stream()
			.filter(c -> "answered".equals(c.getStatus()))
			.mapToLong(WebRTCSignalingHandler.CallInfo::getDurationSeconds)
			.average()
			.orElse(0.0);

		analysis.put("averageCallDuration", avgDuration);

		// 최대 통화 지속시간
		long maxDuration = allCalls.stream()
			.filter(c -> "answered".equals(c.getStatus()))
			.mapToLong(WebRTCSignalingHandler.CallInfo::getDurationSeconds)
			.max()
			.orElse(0L);

		analysis.put("maxCallDuration", maxDuration);

		return analysis;
	}

	/**
	 * 통화 요청 검증 결과
	 */
	public static class CallValidationResult {
		private boolean valid;
		private String reason;

		public CallValidationResult() {}

		public CallValidationResult(boolean valid, String reason) {
			this.valid = valid;
			this.reason = reason;
		}

		public boolean isValid() { return valid; }
		public void setValid(boolean valid) { this.valid = valid; }

		public String getReason() { return reason; }
		public void setReason(String reason) { this.reason = reason; }
	}
}
