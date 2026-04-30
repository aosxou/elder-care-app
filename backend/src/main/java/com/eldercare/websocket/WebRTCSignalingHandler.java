package com.eldercare.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebRTC 시그널링 핸들러
 *
 * WebSocket을 통한 Offer/Answer/ICE Candidate 교환
 * 다중 클라이언트 관리 및 연결 상태 추적
 *
 * 메시지 포맷:
 * {
 *   "type": "offer|answer|ice-candidate|register|hangup|ping",
 *   "from": "사용자ID",
 *   "to": "대상사용자ID",
 *   "sdp": "SDP내용",
 *   "candidate": "ICE Candidate",
 *   "sdpMLineIndex": 0,
 *   "sdpMid": "video"
 * }
 */
@Slf4j
@Component
public class WebRTCSignalingHandler extends TextWebSocketHandler {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 클라이언트 세션 저장소
	 * Key: userId, Value: WebSocketSession
	 */
	private final Map<String, WebSocketSession> clientSessions = new ConcurrentHashMap<>();

	/**
	 * 클라이언트 정보 저장소
	 * Key: userId, Value: ClientInfo
	 */
	private final Map<String, ClientInfo> clientInfo = new ConcurrentHashMap<>();

	/**
	 * 활성 통화 추적
	 * Key: callId, Value: CallInfo
	 */
	private final Map<String, CallInfo> activeCalls = new ConcurrentHashMap<>();

	/**
	 * WebSocket 연결 성공
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		log.info("WebSocket 연결 수립: {}", session.getId());
		// 초기 연결 확인 메시지는 클라이언트가 register 메시지로 전송함
	}

	/**
	 * WebSocket 메시지 수신
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) {
		try {
			String payload = message.getPayload();
			JsonNode messageNode = objectMapper.readTree(payload);

			String type = messageNode.get("type").asText();
			String from = messageNode.get("from").asText();

			log.debug("수신한 메시지 - Type: {}, From: {}", type, from);

			switch (type) {
				case "register" -> handleRegister(session, messageNode);
				case "offer" -> handleOffer(session, messageNode);
				case "answer" -> handleAnswer(session, messageNode);
				case "ice-candidate" -> handleIceCandidate(session, messageNode);
				case "hangup" -> handleHangup(session, messageNode);
				case "ping" -> handlePing(session, messageNode);
				default -> log.warn("알 수 없는 메시지 타입: {}", type);
			}
		} catch (Exception e) {
			log.error("메시지 처리 오류", e);
			try {
				sendErrorMessage(session, "메시지 처리 중 오류 발생: " + e.getMessage());
			} catch (IOException ioException) {
				log.error("에러 메시지 전송 실패", ioException);
			}
		}
	}

	/**
	 * 클라이언트 등록
	 */
	private void handleRegister(WebSocketSession session, JsonNode messageNode) throws IOException {
		String userId = messageNode.get("from").asText();

		// 이전 세션이 있으면 제거
		if (clientSessions.containsKey(userId)) {
			WebSocketSession oldSession = clientSessions.get(userId);
			if (oldSession.isOpen()) {
				oldSession.close();
			}
		}

		// 새 세션 등록
		clientSessions.put(userId, session);

		// 클라이언트 정보 저장
		ClientInfo info = new ClientInfo(
			userId,
			session.getRemoteAddress().getHostName(),
			LocalDateTime.now()
		);
		clientInfo.put(userId, info);

		log.info("클라이언트 등록: {} ({}명 온라인)", userId, clientSessions.size());

		// 등록 확인 메시지 전송
		ObjectNode response = objectMapper.createObjectNode();
		response.put("type", "registered");
		response.put("userId", userId);
		response.put("timestamp", System.currentTimeMillis());

		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));

		// 현재 온라인 사용자 목록 브로드캐스트
		broadcastOnlineUsers();
	}

	/**
	 * Offer 수신 및 처리
	 */
	private void handleOffer(WebSocketSession session, JsonNode messageNode) throws IOException {
		String from = messageNode.get("from").asText();
		String to = messageNode.get("to").asText();
		String sdp = messageNode.get("sdp").asText();

		String callId = generateCallId(from, to);

		log.info("Offer 수신: {} -> {} (Call ID: {})", from, to, callId);

		// 활성 통화 정보 저장
		CallInfo call = new CallInfo(callId, from, to, "offer");
		activeCalls.put(callId, call);

		// 대상 클라이언트에게 Offer 전달
		WebSocketSession targetSession = clientSessions.get(to);
		if (targetSession != null && targetSession.isOpen()) {
			ObjectNode offerMessage = objectMapper.createObjectNode();
			offerMessage.put("type", "offer");
			offerMessage.put("from", from);
			offerMessage.put("to", to);
			offerMessage.put("sdp", sdp);
			offerMessage.put("callId", callId);
			offerMessage.put("timestamp", System.currentTimeMillis());

			targetSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(offerMessage)));

			// Offer 발신자에게 전달 확인
			sendMessageToClient(from, "offer-sent", "Offer가 발신되었습니다");

		} else {
			log.warn("대상 클라이언트를 찾을 수 없음: {}", to);
			sendMessageToClient(from, "error", "대상 사용자를 찾을 수 없습니다: " + to);
		}
	}

	/**
	 * Answer 수신 및 처리
	 */
	private void handleAnswer(WebSocketSession session, JsonNode messageNode) throws IOException {
		String from = messageNode.get("from").asText();
		String to = messageNode.get("to").asText();
		String sdp = messageNode.get("sdp").asText();
		String callId = messageNode.get("callId").asText();

		log.info("Answer 수신: {} -> {} (Call ID: {})", from, to, callId);

		// 활성 통화 상태 업데이트
		if (activeCalls.containsKey(callId)) {
			CallInfo call = activeCalls.get(callId);
			call.setStatus("answered");
			call.setAnswerTime(LocalDateTime.now());
		}

		// 대상 클라이언트에게 Answer 전달
		WebSocketSession targetSession = clientSessions.get(to);
		if (targetSession != null && targetSession.isOpen()) {
			ObjectNode answerMessage = objectMapper.createObjectNode();
			answerMessage.put("type", "answer");
			answerMessage.put("from", from);
			answerMessage.put("to", to);
			answerMessage.put("sdp", sdp);
			answerMessage.put("callId", callId);
			answerMessage.put("timestamp", System.currentTimeMillis());

			targetSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(answerMessage)));

			// Answer 발신자에게 전달 확인
			sendMessageToClient(from, "answer-sent", "Answer가 발신되었습니다");

		} else {
			log.warn("대상 클라이언트를 찾을 수 없음: {}", to);
		}
	}

	/**
	 * ICE Candidate 수신 및 처리
	 */
	private void handleIceCandidate(WebSocketSession session, JsonNode messageNode) throws IOException {
		String from = messageNode.get("from").asText();
		String to = messageNode.get("to").asText();
		String candidate = messageNode.get("candidate").asText();
		int sdpMLineIndex = messageNode.get("sdpMLineIndex", -1).asInt();
		String sdpMid = messageNode.get("sdpMid", "").asText();
		String callId = messageNode.get("callId", "").asText();

		log.debug("ICE Candidate 수신: {} -> {}, Index: {}", from, to, sdpMLineIndex);

		// 대상 클라이언트에게 Candidate 전달
		WebSocketSession targetSession = clientSessions.get(to);
		if (targetSession != null && targetSession.isOpen()) {
			ObjectNode candidateMessage = objectMapper.createObjectNode();
			candidateMessage.put("type", "ice-candidate");
			candidateMessage.put("from", from);
			candidateMessage.put("to", to);
			candidateMessage.put("candidate", candidate);
			candidateMessage.put("sdpMLineIndex", sdpMLineIndex);
			candidateMessage.put("sdpMid", sdpMid);
			candidateMessage.put("callId", callId);
			candidateMessage.put("timestamp", System.currentTimeMillis());

			targetSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(candidateMessage)));

		} else {
			log.warn("ICE Candidate 전달 실패: 대상 클라이언트 없음: {}", to);
		}
	}

	/**
	 * 통화 종료 처리
	 */
	private void handleHangup(WebSocketSession session, JsonNode messageNode) throws IOException {
		String from = messageNode.get("from").asText();
		String to = messageNode.get("to").asText();
		String callId = messageNode.get("callId", "").asText();

		log.info("Hangup 수신: {} -> {} (Call ID: {})", from, to, callId);

		// 활성 통화 정보 업데이트
		if (!callId.isEmpty() && activeCalls.containsKey(callId)) {
			CallInfo call = activeCalls.get(callId);
			call.setStatus("ended");
			call.setEndTime(LocalDateTime.now());
		}

		// 대상 클라이언트에게 Hangup 전달
		WebSocketSession targetSession = clientSessions.get(to);
		if (targetSession != null && targetSession.isOpen()) {
			ObjectNode hangupMessage = objectMapper.createObjectNode();
			hangupMessage.put("type", "hangup");
			hangupMessage.put("from", from);
			hangupMessage.put("to", to);
			hangupMessage.put("callId", callId);
			hangupMessage.put("timestamp", System.currentTimeMillis());

			targetSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(hangupMessage)));
		}

		// 통화 기록 정리 (1분 후)
		if (!callId.isEmpty()) {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					activeCalls.remove(callId);
				}
			}, 60000);
		}
	}

	/**
	 * Ping/Pong 처리 (연결 유지)
	 */
	private void handlePing(WebSocketSession session, JsonNode messageNode) throws IOException {
		String from = messageNode.get("from").asText();

		ObjectNode pongMessage = objectMapper.createObjectNode();
		pongMessage.put("type", "pong");
		pongMessage.put("timestamp", System.currentTimeMillis());

		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pongMessage)));

		// 클라이언트 활동 시간 업데이트
		if (clientInfo.containsKey(from)) {
			clientInfo.get(from).setLastActivity(LocalDateTime.now());
		}
	}

	/**
	 * WebSocket 연결 해제
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		// 세션에 매핑된 userId 찾기
		String userId = null;
		for (Map.Entry<String, WebSocketSession> entry : clientSessions.entrySet()) {
			if (entry.getValue().equals(session)) {
				userId = entry.getKey();
				break;
			}
		}

		if (userId != null) {
			clientSessions.remove(userId);
			clientInfo.remove(userId);
			log.info("클라이언트 연결 해제: {} ({}명 남음, Status: {})",
				userId, clientSessions.size(), status.getCode());

			// 온라인 사용자 목록 업데이트
			try {
				broadcastOnlineUsers();
			} catch (IOException e) {
				log.error("온라인 사용자 목록 브로드캐스트 실패", e);
			}
		}
	}

	/**
	 * 오류 처리
	 */
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		log.error("WebSocket 전송 오류: {}", session.getId(), exception);
	}

	/**
	 * 특정 클라이언트에게 메시지 전송
	 */
	private void sendMessageToClient(String userId, String type, String message) throws IOException {
		WebSocketSession session = clientSessions.get(userId);
		if (session != null && session.isOpen()) {
			ObjectNode msg = objectMapper.createObjectNode();
			msg.put("type", type);
			msg.put("message", message);
			msg.put("timestamp", System.currentTimeMillis());

			session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
		}
	}

	/**
	 * 에러 메시지 전송
	 */
	private void sendErrorMessage(WebSocketSession session, String errorMessage) throws IOException {
		if (session.isOpen()) {
			ObjectNode error = objectMapper.createObjectNode();
			error.put("type", "error");
			error.put("message", errorMessage);
			error.put("timestamp", System.currentTimeMillis());

			session.sendMessage(new TextMessage(objectMapper.writeValueAsString(error)));
		}
	}

	/**
	 * 온라인 사용자 목록 브로드캐스트
	 */
	private void broadcastOnlineUsers() throws IOException {
		List<String> onlineUsers = new ArrayList<>(clientSessions.keySet());

		ObjectNode broadcast = objectMapper.createObjectNode();
		broadcast.put("type", "users-online");
		broadcast.putPOJO("users", onlineUsers);
		broadcast.put("count", onlineUsers.size());
		broadcast.put("timestamp", System.currentTimeMillis());

		String message = objectMapper.writeValueAsString(broadcast);

		for (WebSocketSession session : clientSessions.values()) {
			if (session.isOpen()) {
				session.sendMessage(new TextMessage(message));
			}
		}

		log.debug("온라인 사용자 목록 브로드캐스트: {} 명", onlineUsers.size());
	}

	/**
	 * Call ID 생성
	 */
	private String generateCallId(String from, String to) {
		return from.compareTo(to) < 0 ?
			from + "-" + to + "-" + System.currentTimeMillis() :
			to + "-" + from + "-" + System.currentTimeMillis();
	}

	/**
	 * 활성 통화 정보 조회
	 */
	public CallInfo getCallInfo(String callId) {
		return activeCalls.get(callId);
	}

	/**
	 * 모든 활성 통화 조회
	 */
	public List<CallInfo> getAllActiveCalls() {
		return new ArrayList<>(activeCalls.values());
	}

	/**
	 * 온라인 클라이언트 목록 조회
	 */
	public List<String> getOnlineUsers() {
		return new ArrayList<>(clientSessions.keySet());
	}

	/**
	 * 클라이언트 정보 조회
	 */
	public ClientInfo getClientInfo(String userId) {
		return clientInfo.get(userId);
	}

	/**
	 * 클라이언트 연결 상태 확인
	 */
	public boolean isClientOnline(String userId) {
		return clientSessions.containsKey(userId) && clientSessions.get(userId).isOpen();
	}

	/**
	 * 연결된 총 클라이언트 수
	 */
	public int getConnectedClientCount() {
		return clientSessions.size();
	}

	// ===== Inner Classes =====

	/**
	 * 클라이언트 정보
	 */
	public static class ClientInfo {
		private final String userId;
		private final String ipAddress;
		private LocalDateTime connectedAt;
		private LocalDateTime lastActivity;

		public ClientInfo(String userId, String ipAddress, LocalDateTime connectedAt) {
			this.userId = userId;
			this.ipAddress = ipAddress;
			this.connectedAt = connectedAt;
			this.lastActivity = connectedAt;
		}

		// Getters and Setters
		public String getUserId() { return userId; }
		public String getIpAddress() { return ipAddress; }
		public LocalDateTime getConnectedAt() { return connectedAt; }
		public LocalDateTime getLastActivity() { return lastActivity; }
		public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
	}

	/**
	 * 활성 통화 정보
	 */
	public static class CallInfo {
		private final String callId;
		private final String from;
		private final String to;
		private String status;  // offer, answered, ended
		private LocalDateTime offerTime;
		private LocalDateTime answerTime;
		private LocalDateTime endTime;

		public CallInfo(String callId, String from, String to, String status) {
			this.callId = callId;
			this.from = from;
			this.to = to;
			this.status = status;
			this.offerTime = LocalDateTime.now();
		}

		// Getters and Setters
		public String getCallId() { return callId; }
		public String getFrom() { return from; }
		public String getTo() { return to; }
		public String getStatus() { return status; }
		public void setStatus(String status) { this.status = status; }
		public LocalDateTime getOfferTime() { return offerTime; }
		public LocalDateTime getAnswerTime() { return answerTime; }
		public void setAnswerTime(LocalDateTime answerTime) { this.answerTime = answerTime; }
		public LocalDateTime getEndTime() { return endTime; }
		public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

		/**
		 * 통화 지속 시간 (초)
		 */
		public long getDurationSeconds() {
			LocalDateTime end = endTime != null ? endTime : LocalDateTime.now();
			LocalDateTime start = answerTime != null ? answerTime : offerTime;
			return java.time.temporal.ChronoUnit.SECONDS.between(start, end);
		}
	}
}
