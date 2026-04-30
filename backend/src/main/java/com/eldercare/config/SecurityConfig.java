package com.eldercare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 설정 클래스
 *
 * TODO: JWT 토큰 기반 인증 구현
 * TODO: 사용자 권한 관리 (ADMIN, GUARDIAN, CAREGIVER, ELDERLY)
 * TODO: CSRF 보호
 * TODO: 비밀번호 정책 강화
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)  // TODO: @PreAuthorize, @Secured 활성화
public class SecurityConfig {

	/**
	 * 비밀번호 인코더 설정
	 * BCrypt를 사용하여 비밀번호 암호화
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 인증 매니저 설정
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	/**
	 * HTTP 보안 필터 체인 설정
	 *
	 * TODO: JWT 토큰 필터 추가
	 * TODO: 엔드포인트별 접근 제어
	 * TODO: 세션 정책 설정
	 * TODO: CORS 설정
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// CSRF 보호 비활성화 (JWT 사용하므로)
				.csrf(csrf -> csrf.disable())

				// CORS 설정
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))

				// 세션 관리
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // JWT 사용하므로 상태 없음
				)

				// 권한 설정
				.authorizeHttpRequests(authz -> authz
						// 공개 엔드포인트
						.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/api/public/**").permitAll()
						.requestMatchers("/actuator/health").permitAll()
						.requestMatchers("/ws/**").permitAll()  // WebSocket 연결

						// 보호된 엔드포인트
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.requestMatchers("/api/guardian/**").hasAnyRole("GUARDIAN", "ADMIN")
						.requestMatchers("/api/caregiver/**").hasAnyRole("CAREGIVER", "ADMIN")
						.requestMatchers("/api/elderly/**").hasAnyRole("ELDERLY", "ADMIN")

						// 그 외 모든 요청은 인증 필요
						.anyRequest().authenticated()
				)

				// 에러 처리
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((request, response, authException) -> {
							// TODO: 인증 실패 시 응답 처리
							response.setStatus(401);
							response.setContentType("application/json");
							response.getWriter().write("{\"error\": \"Unauthorized\"}");
						})
						.accessDeniedHandler((request, response, accessDeniedException) -> {
							// TODO: 권한 부족 시 응답 처리
							response.setStatus(403);
							response.setContentType("application/json");
							response.getWriter().write("{\"error\": \"Forbidden\"}");
						})
				);

		// TODO: JWT 토큰 필터 추가
		// http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * CORS 설정
	 *
	 * TODO: 프로덕션 환경에서 허용 도메인 제한
	 * TODO: 허용 HTTP 메서드 설정
	 * TODO: 허용 헤더 설정
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfig = new CorsConfiguration();

		// 허용 오리진
		corsConfig.setAllowedOrigins(Arrays.asList(
				"http://localhost:3000",      // 웹 프론트엔드
				"http://localhost:8080",      // 개발 서버
				"http://localhost:5173",      // Vite dev server
				"http://127.0.0.1:3000"
		));

		// 허용 HTTP 메서드
		corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

		// 허용 헤더
		corsConfig.setAllowedHeaders(Arrays.asList(
				"*",
				"Authorization",
				"Content-Type",
				"X-Requested-With",
				"Accept"
		));

		// 응답 헤더에 포함
		corsConfig.setExposedHeaders(Arrays.asList(
				"Authorization",
				"X-Total-Count",
				"X-Page-Number"
		));

		// 자격 증명 허용
		corsConfig.setAllowCredentials(true);

		// 캐시 시간 (초)
		corsConfig.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);

		return source;
	}

	/*
	 * TODO: JWT 토큰 필터 구현
	 * @Bean
	 * public JwtAuthenticationFilter jwtAuthenticationFilter() {
	 *     return new JwtAuthenticationFilter();
	 * }
	 */

	/*
	 * TODO: 사용자 정의 UserDetailsService 구현
	 * @Bean
	 * public UserDetailsService userDetailsService() {
	 *     return new CustomUserDetailsService();
	 * }
	 */
}
