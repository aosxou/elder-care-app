package com.eldercare.backend.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    // 단일 기기에 알림 전송
    public void sendNotification(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("FCM 전송 성공: " + response);
        } catch (Exception e) {
            System.err.println("FCM 전송 실패: " + e.getMessage());
        }
    }

    // 매일 아침 9시 자동 알림
    @Scheduled(cron = "0 0 9 * * *")
    public void sendMorningCall() {
        System.out.println("매일 9시 알림 전송 시작!");
        // TODO: DB에서 FCM 토큰 목록 가져와서 전송
        // List<String> tokens = elderRepository.findAllFcmTokens();
        // tokens.forEach(token -> sendNotification(token, "안부 전화 📞", "좋은 아침이에요!"));
    }
}