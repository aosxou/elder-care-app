import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  // Android 에뮬레이터에서 PC 서버 접속: 10.0.2.2
  // 실기기에서: 172.20.10.2
  static const String baseUrl = 'http://10.0.2.2:8080';

  // 통화 내역 불러오기 (최근 10개)
  static Future<List<dynamic>> getCallHistory() async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/api/call-history/recent'),
        headers: {
          'Content-Type': 'application/json',
        },
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('요청 시간 초과'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('통화 내역 불러오기 실패: ${response.statusCode}');
      }
    } catch (e) {
      print('API 에러: $e');
      rethrow;
    }
  }

  // 통화 기록 저장
  static Future<void> saveCallHistory({
    required String callerName,
    required String callerType,
    required int duration,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/api/call-history'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'callerName': callerName,
          'callerType': callerType,
          'duration': duration,
          'status': 'COMPLETED',
          'timestamp': DateTime.now().toIso8601String(),
        }),
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('요청 시간 초과'),
      );

      if (response.statusCode != 201 && response.statusCode != 200) {
        throw Exception('통화 기록 저장 실패: ${response.statusCode}');
      }
      print('통화 기록 저장 완료');
    } catch (e) {
      print('API 에러: $e');
      rethrow;
    }
  }

  // FCM 토큰 등록
  static Future<void> registerFcmToken(String token) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/api/fcm/register'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'token': token}),
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('요청 시간 초과'),
      );

      if (response.statusCode != 200 && response.statusCode != 201) {
        throw Exception('FCM 토큰 등록 실패: ${response.statusCode}');
      }
      print('FCM 토큰 등록 완료: $token');
    } catch (e) {
      print('API 에러: $e');
    }
  }

  // AI 대화 (Gemini 연동)
  static Future<String> chat(String message) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/api/conversation'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'message': message}),
      ).timeout(
        const Duration(seconds: 15),
        onTimeout: () => throw Exception('요청 시간 초과'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['response'] ?? '응답을 받을 수 없습니다.';
      }
      throw Exception('AI 대화 실패: ${response.statusCode}');
    } catch (e) {
      print('API 에러: $e');
      rethrow;
    }
  }

  // TTS (텍스트 → 음성) - 향후 구현
  static Future<String> textToSpeech(String text) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/api/tts'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'text': text}),
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('요청 시간 초과'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['audioUrl'] ?? '';
      }
      throw Exception('TTS 변환 실패: ${response.statusCode}');
    } catch (e) {
      print('API 에러: $e');
      rethrow;
    }
  }

  // 어르신 정보 조회
  static Future<Map<String, dynamic>> getElderInfo(String elderId) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/api/elders/$elderId'),
        headers: {'Content-Type': 'application/json'},
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () => throw Exception('요청 시간 초과'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      throw Exception('어르신 정보 조회 실패: ${response.statusCode}');
    } catch (e) {
      print('API 에러: $e');
      rethrow;
    }
  }
}
