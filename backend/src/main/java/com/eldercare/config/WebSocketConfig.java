package com.eldercare.config;

import com.eldercare.websocket.WebRTCSignalingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정 클래스
 *
 * STOMP 기반 메시지 브로커와 WebRTC 시그널링 핸들러 통합
 * - STOMP: 실시간 메시징 (채팅, 알림, 건강 데이터)
 * - WebSocket Handler: WebRTC 시그널링 (Offer/Answer/ICE)
 *
 * TODO: 인터셉터 추가 (인증, 로깅)
 * TODO: 메시지 레이트 제한
 * TODO: 실시간 통신 최적화
 */
@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {

	@Autowired
	private WebRTCSignalingHandler webrtcSignalingHandler;

	/**
	 * 메시지 브로커 설정
	 * - /app: 클라이언트로부터 서버로 보낼 메시지 prefix
	 * - /topic, /queue: 서버에서 클라이언트로 보낼 메시지 prefix
	 *
	 * TODO: 브로커 relay 설정 (RabbitMQ, ActiveMQ)
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// 메모리 기반 메시지 브로커 설정
		config.enableSimpleBroker("/topic", "/queue")
				.setHeartbeatValue(new long[]{10000, 10000});

		// 클라이언트로부터의 메시지 prefix
		config.setApplicationDestinationPrefixes("/app");

		// TODO: 사용자 지정 메시지 변환기 설정

		/*
		 * TODO: 외부 메시지 브로커 연동
		 * config.enableStompBrokerRelay("/topic", "/queue")
		 * 		.setRelayHost("rabbitmq.example.com")
		 * 		.setRelayPort(61613)
		 * 		.setSystemLogin("guest")
		 * 		.setSystemPasscode("guest");
		 */
	}

	/**
	 * WebSocket 핸들러 등록
	 *
	 * WebRTC 시그널링용 WebSocket 핸들러를 등록
	 * - /webrtc: WebRTC 시그널링 (Offer/Answer/ICE)
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(webrtcSignalingHandler, "/webrtc")
				.setAllowedOrigins(
						"http://localhost:3000",
						"http://localhost:8080",
						"http://localhost:5173"
				);
	}

	/**
	 * STOMP 엔드포인트 등록
	 *
	 * - /ws: 메인 STOMP 연결 엔드포인트
	 *   - /app: 클라이언트 → 서버 메시지
	 *   - /topic, /queue: 서버 → 클라이언트 메시지
	 *
	 * 용도별 분류:
	 * - /topic/chat/{conversationId}: 실시간 채팅
	 * - /topic/notifications: 알림
	 * - /topic/health-updates: 건강 데이터 실시간 업데이트
	 * - /queue/messages: 개인 메시지
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 메인 WebSocket 연결 엔드포인트 (STOMP)
		registry.addEndpoint("/ws")
				.setAllowedOrigins(
						"http://localhost:3000",
						"http://localhost:8080",
						"http://localhost:5173"  // Vite dev server
				)
				.withSockJS()  // SockJS fallback (WebSocket 미지원 환경)
				.setSessionCookieNeeded(true)
				.setHeartbeatTime(25000);
	}

	/*
	 * TODO: 채널 인터셉터 추가
	 * - 메시지 검증
	 * - 사용자 인증 확인
	 * - 메시지 로깅
	 * - 메시지 레이트 제한
	 *
	 * 사용 방법:
	 * @Override
	 * public void configureClientInboundChannel(ChannelRegistration registration) {
	 *     registration.interceptors(new ChannelInterceptor() {
	 *         @Override
	 *         public Message<?> preSend(Message<?> message, MessageChannel channel) {
	 *             // 메시지 검증 로직
	 *             return message;
	 *         }
	 *     });
	 * }
	 */

	/*
	 * WebRTC 시그널링 메시지 포맷:
	 *
	 * 등록:
	 * {
	 *   "type": "register",
	 *   "from": "userId"
	 * }
	 *
	 * Offer 발신:
	 * {
	 *   "type": "offer",
	 *   "from": "senderId",
	 *   "to": "receiverId",
	 *   "sdp": "v=0\r\no=...",
	 *   "callId": "id"
	 * }
	 *
	 * Answer 발신:
	 * {
	 *   "type": "answer",
	 *   "from": "senderId",
	 *   "to": "receiverId",
	 *   "sdp": "v=0\r\no=...",
	 *   "callId": "id"
	 * }
	 *
	 * ICE Candidate:
	 * {
	 *   "type": "ice-candidate",
	 *   "from": "senderId",
	 *   "to": "receiverId",
	 *   "candidate": "candidate=...",
	 *   "sdpMLineIndex": 0,
	 *   "sdpMid": "video",
	 *   "callId": "id"
	 * }
	 *
	 * Hangup:
	 * {
	 *   "type": "hangup",
	 *   "from": "senderId",
	 *   "to": "receiverId",
	 *   "callId": "id"
	 * }
	 */
}
