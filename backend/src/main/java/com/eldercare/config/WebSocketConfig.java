package com.eldercare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정 클래스
 *
 * TODO: STOMP 엔드포인트 설정
 * TODO: 메시지 브로커 설정
 * TODO: 인터셉터 추가 (인증, 로깅)
 * TODO: 실시간 통신 최적화
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

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
	 * STOMP 엔드포인트 등록
	 *
	 * TODO: 엔드포인트별 역할 정의
	 * - /ws/chat: 실시간 채팅
	 * - /ws/notification: 알림
	 * - /ws/call: WebRTC 시그널링
	 * - /ws/health: 건강 데이터 실시간 업데이트
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// WebSocket 연결 엔드포인트
		registry.addEndpoint("/ws")
				.setAllowedOrigins(
						"http://localhost:3000",
						"http://localhost:8080",
						"http://localhost:5173"  // Vite dev server
				)
				.withSockJS()  // SockJS fallback (WebSocket 미지원 환경)
				.setSessionCookieNeeded(true)
				.setHeartbeatTime(25000);

		// TODO: 엔드포인트별 추가 설정
		// registry.addEndpoint("/ws/chat").setAllowedOrigins(...).withSockJS();
		// registry.addEndpoint("/ws/call").setAllowedOrigins(...).withSockJS();
	}

	/*
	 * TODO: 채널 인터셉터 추가
	 * - 메시지 검증
	 * - 사용자 인증 확인
	 * - 메시지 로깅
	 * - 메시지 레이트 제한
	 */
}
