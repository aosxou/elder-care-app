package com.eldercare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.ComponentScan;

/**
 * Elder Care Management System - Spring Boot 메인 애플리케이션
 *
 * TODO: 로그 설정 초기화
 * TODO: 스케줄러 설정
 * TODO: 캐시 설정
 */
@SpringBootApplication
@EnableScheduling  // TODO: 스케줄된 작업 활성화 (알림, 정기 업데이트)
public class ElderCareApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElderCareApplication.class, args);
		System.out.println("=".repeat(50));
		System.out.println("Elder Care Application Started Successfully!");
		System.out.println("=".repeat(50));
	}

	/*
	 * TODO: 애플리케이션 초기화 로직
	 * - 데이터베이스 마이그레이션
	 * - 캐시 초기화
	 * - 설정 로드
	 */

}
