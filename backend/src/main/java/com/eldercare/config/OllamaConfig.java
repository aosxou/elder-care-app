package com.eldercare.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaApi;

/**
 * Ollama AI 설정 클래스 (Spring AI 통합)
 *
 * TODO: 모델 성능 최적화
 * TODO: 토큰 제한 설정
 * TODO: 요청 타임아웃 설정
 * TODO: 에러 처리 및 재시도 로직
 * TODO: 캐싱 설정
 */
@Configuration
public class OllamaConfig {

	/**
	 * Ollama API 클라이언트 설정
	 *
	 * TODO: Ollama 서버 상태 확인
	 * TODO: 모델 로드 상태 확인
	 * TODO: 커넥션 풀 설정
	 */
	@Bean
	public OllamaApi ollamaApi() {
		// Ollama 서버 주소 (기본: http://localhost:11434)
		return new OllamaApi("http://localhost:11434");
	}

	/**
	 * Ollama Chat 클라이언트 설정
	 *
	 * TODO: 다양한 모델 지원
	 * - mistral: 일반적인 대화
	 * - neural-chat: 한국어 최적화
	 * - llama2: 길이 있는 응답
	 * - openhermes: 더 정확한 응답
	 */
	@Bean
	public OllamaChatClient ollamaChatClient(OllamaApi ollamaApi) {
		return new OllamaChatClient(ollamaApi)
				.withModel("mistral");  // TODO: 환경 변수로 모델 선택
	}

	/*
	 * TODO: AI 모델 프로퍼티 클래스
	 *
	 * @ConfigurationProperties(prefix = "app.ai")
	 * @Getter
	 * @Setter
	 * public static class AiProperties {
	 *     private String baseUrl;          // Ollama 서버 URL
	 *     private String model;             // 사용할 모델
	 *     private Integer maxTokens;        // 최대 토큰 수
	 *     private Double temperature;       // 창의성 (0.0~1.0)
	 *     private Double topP;              // Top-P 샘플링
	 *     private Integer timeout;          // 요청 타임아웃 (초)
	 *     private Boolean streaming;        // 스트리밍 지원 여부
	 *     private Integer retryAttempts;    // 재시도 횟수
	 *     private Integer retryDelay;       // 재시도 간격 (밀리초)
	 * }
	 */

	/*
	 * TODO: AI 모델 성능 설정
	 * - 동시 요청 제한
	 * - 요청 큐 설정
	 * - 응답 캐싱
	 * - 임베딩 캐싱
	 */

	/*
	 * TODO: 모델 선택 로직
	 * - 요청 유형별 최적 모델 선택
	 * - 부하 기반 모델 선택
	 * - 사용자 선호도 기반 선택
	 */

	/*
	 * TODO: 프롬프트 템플릿
	 * - 시스템 역할 설정
	 * - 컨텍스트 주입
	 * - 응답 포맷 지정
	 */
}
