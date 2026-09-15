import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

const Color eBg = Color(0xFFFBF6ED);
const Color eCard = Color(0xFFFFFDF8);
const Color eInk = Color(0xFF3B2F26);
const Color eInkSoft = Color(0xFF93816D);
const Color eLine = Color(0xFFEADFC9);
const Color eAccent = Color(0xFFD97B4F);

class ElderlyDetailScreen extends StatefulWidget {
  final int elderlyId;
  final String elderlyName;

  const ElderlyDetailScreen({
    super.key,
    required this.elderlyId,
    required this.elderlyName,
  });

  @override
  State<ElderlyDetailScreen> createState() => _ElderlyDetailScreenState();
}

class _ElderlyDetailScreenState extends State<ElderlyDetailScreen> {
  String _selectedDate = '2024-12-15';

  // 날짜별 더미 데이터
  final Map<String, Map<String, dynamic>> _conversationData = {
    '2024-12-15': {
      'summary':
          '오늘 오전 9시 30분부터 저녁 6시 45분까지 약 9시간 동안 총 3회의 대화가 이루어졌습니다. 아침에는 날씨가 좋다며 긍정적인 감정으로 하루를 시작했고, 낮 시간에는 손주를 만나고 싶은 마음을 표현했습니다. 저녁에는 AI 친구와의 통화로 활발한 상호작용을 보였습니다.',
      'depressionIndex': '2.4/10',
      'depressionStatus': '정상',
      'conversationCount': '3회',
      'conversationStatus': '활발함',
      'activityStatus': '정상',
      'activitySteps': '7,245걸음',
      'healthStatus': '정상',
      'healthDetail': '이상 없음',
      'conversations': [
        {
          'time': '09:30',
          'content': '오늘 날씨가 정말 좋네요',
          'sentiment': '긍정적',
        },
        {
          'time': '14:15',
          'content': '손주가 만났다고 얘기해주면 좋겠어요',
          'sentiment': '중립적',
        },
        {
          'time': '18:45',
          'content': 'AI 친구와 통화했습니다',
          'sentiment': '긍정적',
        },
      ],
    },
    '2024-12-14': {
      'summary':
          '어제는 오전 10시부터 오후 4시까지 약 6시간 동안 2회의 대화가 있었습니다. 아침에는 다소 조용한 모습을 보였으나, 오후에 활동적인 대화를 나누며 기분이 좋아졌습니다. 수면이 충분했으며 활동량도 적절한 상태입니다.',
      'depressionIndex': '3.1/10',
      'depressionStatus': '정상',
      'conversationCount': '2회',
      'conversationStatus': '정상',
      'activityStatus': '정상',
      'activitySteps': '5,800걸음',
      'healthStatus': '정상',
      'healthDetail': '이상 없음',
      'conversations': [
        {
          'time': '10:15',
          'content': '좀 피곤해요',
          'sentiment': '중립적',
        },
        {
          'time': '16:30',
          'content': '산책하고 기분이 좋아졌어요',
          'sentiment': '긍정적',
        },
      ],
    },
    '2024-12-13': {
      'summary':
          '2일 전에는 오전 9시부터 저녁 8시까지 총 4회의 대화가 이루어졌습니다. 하루 종일 활발한 상호작용을 보였으며, 특히 손자와의 대화 시간이 길었습니다. 전반적으로 긍정적인 감정 상태를 유지했습니다.',
      'depressionIndex': '2.0/10',
      'depressionStatus': '정상',
      'conversationCount': '4회',
      'conversationStatus': '매우 활발함',
      'activityStatus': '활동적',
      'activitySteps': '9,120걸음',
      'healthStatus': '정상',
      'healthDetail': '이상 없음',
      'conversations': [
        {
          'time': '09:00',
          'content': '손자가 방문했어요',
          'sentiment': '매우긍정적',
        },
        {
          'time': '11:45',
          'content': '함께 점심을 먹었습니다',
          'sentiment': '긍정적',
        },
        {
          'time': '15:20',
          'content': '같이 산책했어요',
          'sentiment': '긍정적',
        },
        {
          'time': '19:30',
          'content': '함께 저녁을 먹고 헤어졌어요',
          'sentiment': '긍정적',
        },
      ],
    },
  };

  @override
  Widget build(BuildContext context) {
    // 선택한 날짜의 데이터 가져오기
    final data = _conversationData[_selectedDate] ?? _conversationData['2024-12-15']!;
    final conversations = data['conversations'] as List<Map<String, String>>;
    return Scaffold(
      backgroundColor: eBg,
      appBar: AppBar(
        backgroundColor: eBg,
        elevation: 0,
        title: Text(
          '${widget.elderlyName} 건강 리포트',
          style: GoogleFonts.notoSerifKr(
            fontSize: 24,
            fontWeight: FontWeight.w700,
            color: eInk,
          ),
        ),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_rounded, color: eInk),
          onPressed: () => Navigator.pop(context),
        ),
      ),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // 날짜 선택
              GestureDetector(
                onTap: () async {
                  final DateTime? picked = await showDatePicker(
                    context: context,
                    initialDate: DateTime.parse(_selectedDate),
                    firstDate: DateTime(2024, 1, 1),
                    lastDate: DateTime.now(),
                    locale: const Locale('ko', 'KR'),
                    builder: (context, child) {
                      return Theme(
                        data: Theme.of(context).copyWith(
                          colorScheme: const ColorScheme.light(
                            primary: eAccent,
                            onPrimary: Colors.white,
                            onSurface: eInk,
                          ),
                          textTheme: Theme.of(context).textTheme.apply(
                            fontFamily: 'NotoSansKR',
                          ),
                        ),
                        child: Localizations.override(
                          context: context,
                          locale: const Locale('ko', 'KR'),
                          child: child!,
                        ),
                      );
                    },
                  );
                  if (picked != null) {
                    setState(() {
                      _selectedDate = picked.toString().split(' ')[0];
                    });
                  }
                },
                child: Container(
                  decoration: BoxDecoration(
                    color: eCard,
                    border: Border.all(color: eAccent, width: 2),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  child: Row(
                    children: [
                      const Icon(Icons.calendar_today_rounded, color: eAccent),
                      const SizedBox(width: 12),
                      Text(
                        _selectedDate,
                        style: GoogleFonts.notoSansKr(
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                          color: eInk,
                        ),
                      ),
                      const Spacer(),
                      const Icon(
                        Icons.arrow_forward_ios_rounded,
                        size: 16,
                        color: eAccent,
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 24),

              // 통계 제목
              Text(
                '오늘의 통계',
                style: GoogleFonts.notoSerifKr(
                  fontSize: 18,
                  fontWeight: FontWeight.w700,
                  color: eInk,
                ),
              ),
              const SizedBox(height: 12),

              // 통계 그리드
              Row(
                children: [
                  Expanded(
                    child: _buildStatCard(
                      title: '우울 수치',
                      value: data['depressionIndex'],
                      subtitle: data['depressionStatus'],
                      color: const Color(0xFF4CAF50),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: _buildStatCard(
                      title: '대화 횟수',
                      value: data['conversationCount'],
                      subtitle: data['conversationStatus'],
                      color: eAccent,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              Row(
                children: [
                  Expanded(
                    child: _buildStatCard(
                      title: '활동량',
                      value: data['activityStatus'],
                      subtitle: data['activitySteps'],
                      color: const Color(0xFF2196F3),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: _buildStatCard(
                      title: '건강상태',
                      value: data['healthStatus'],
                      subtitle: data['healthDetail'],
                      color: const Color(0xFF4CAF50),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 24),

              // 전체 대화 요약
              Text(
                '오늘의 대화 요약',
                style: GoogleFonts.notoSerifKr(
                  fontSize: 18,
                  fontWeight: FontWeight.w700,
                  color: eInk,
                ),
              ),
              const SizedBox(height: 12),
              Container(
                decoration: BoxDecoration(
                  color: eAccent.withOpacity(0.08),
                  border: Border.all(
                    color: eAccent.withOpacity(0.2),
                  ),
                  borderRadius: BorderRadius.circular(12),
                ),
                padding: const EdgeInsets.all(16),
                child: Text(
                  data['summary'],
                  style: TextStyle(
                    fontSize: 14,
                    color: eInk,
                    height: 1.8,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
              const SizedBox(height: 24),

              // 대화 내용
              Text(
                '시간별 대화 기록',
                style: GoogleFonts.notoSerifKr(
                  fontSize: 18,
                  fontWeight: FontWeight.w700,
                  color: eInk,
                ),
              ),
              const SizedBox(height: 12),
              Container(
                decoration: BoxDecoration(
                  color: eCard,
                  border: Border.all(color: eLine),
                  borderRadius: BorderRadius.circular(12),
                ),
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    ...List.generate(
                      conversations.length,
                      (index) => Column(
                        children: [
                          _buildConversationItem(
                            time: conversations[index]['time']!,
                            content: conversations[index]['content']!,
                            sentiment: conversations[index]['sentiment']!,
                          ),
                          if (index < conversations.length - 1)
                            const SizedBox(height: 12),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 24),

              // 상태 분석
              Text(
                '상태 분석',
                style: GoogleFonts.notoSerifKr(
                  fontSize: 18,
                  fontWeight: FontWeight.w700,
                  color: eInk,
                ),
              ),
              const SizedBox(height: 12),
              Container(
                decoration: BoxDecoration(
                  color: data['depressionStatus'] == '정상'
                      ? const Color(0xFF4CAF50).withOpacity(0.1)
                      : const Color(0xFFFFA726).withOpacity(0.1),
                  border: Border.all(
                    color: data['depressionStatus'] == '정상'
                        ? const Color(0xFF4CAF50).withOpacity(0.3)
                        : const Color(0xFFFFA726).withOpacity(0.3),
                  ),
                  borderRadius: BorderRadius.circular(12),
                ),
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Icon(
                          data['depressionStatus'] == '정상'
                              ? Icons.check_circle_outline_rounded
                              : Icons.warning_amber_rounded,
                          color: data['depressionStatus'] == '정상'
                              ? const Color(0xFF4CAF50)
                              : const Color(0xFFFFA726),
                          size: 24,
                        ),
                        const SizedBox(width: 12),
                        Text(
                          data['depressionStatus'] == '정상'
                              ? '정상 상태'
                              : '주의 필요',
                          style: GoogleFonts.notoSansKr(
                            fontSize: 16,
                            fontWeight: FontWeight.w700,
                            color: data['depressionStatus'] == '정상'
                                ? const Color(0xFF4CAF50)
                                : const Color(0xFFFFA726),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),
                    Text(
                      data['depressionStatus'] == '정상'
                          ? '• 대화 활동이 활발함\n• 수면 시간이 충분함\n• 특이사항 없음'
                          : '• 우울 수치 증가\n• 활동량 감소 주의\n• 보호자 연락 권장',
                      style: const TextStyle(
                        fontSize: 14,
                        color: eInkSoft,
                        height: 1.6,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildStatCard({
    required String title,
    required String value,
    required String subtitle,
    required Color color,
  }) {
    return Container(
      decoration: BoxDecoration(
        color: eCard,
        border: Border.all(color: eLine),
        borderRadius: BorderRadius.circular(12),
      ),
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: const TextStyle(
              fontSize: 12,
              color: eInkSoft,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            value,
            style: GoogleFonts.notoSansKr(
              fontSize: 20,
              fontWeight: FontWeight.w700,
              color: color,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            subtitle,
            style: const TextStyle(
              fontSize: 12,
              color: eInkSoft,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildConversationItem({
    required String time,
    required String content,
    required String sentiment,
  }) {
    Color sentimentColor = sentiment == '긍정적'
        ? const Color(0xFF4CAF50)
        : sentiment == '부정적'
            ? const Color(0xFFF44336)
            : const Color(0xFFFFA726);

    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          time,
          style: const TextStyle(
            fontSize: 12,
            color: eInkSoft,
            fontWeight: FontWeight.w600,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                content,
                style: const TextStyle(
                  fontSize: 13,
                  color: eInk,
                ),
              ),
              const SizedBox(height: 4),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                decoration: BoxDecoration(
                  color: sentimentColor.withOpacity(0.15),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  sentiment,
                  style: TextStyle(
                    fontSize: 11,
                    color: sentimentColor,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}
