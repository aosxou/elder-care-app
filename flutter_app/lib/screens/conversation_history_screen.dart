import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'conversation_detail_screen.dart';

const Color eBg = Color(0xFFFBF6ED);
const Color eCard = Color(0xFFFFFDF8);
const Color eInk = Color(0xFF3B2F26);
const Color eInkSoft = Color(0xFF93816D);
const Color eLine = Color(0xFFEADFC9);
const Color eAccent = Color(0xFFD97B4F);

class ConversationHistoryScreen extends StatelessWidget {
  const ConversationHistoryScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final List<Map<String, dynamic>> conversations = [
      {
        'date': '2024-12-15',
        'time': '14:30',
        'elder': '김할머니',
        'topic': '일상 대화',
        'sentiment': '긍정적',
        'duration': '15분',
      },
      {
        'date': '2024-12-15',
        'time': '09:45',
        'elder': '이할아버지',
        'topic': '건강 확인',
        'sentiment': '중립적',
        'duration': '10분',
      },
      {
        'date': '2024-12-14',
        'time': '18:20',
        'elder': '김할머니',
        'topic': '추억 나누기',
        'sentiment': '긍정적',
        'duration': '25분',
      },
      {
        'date': '2024-12-14',
        'time': '11:15',
        'elder': '이할아버지',
        'topic': '약물 복용 알림',
        'sentiment': '중립적',
        'duration': '5분',
      },
    ];

    return Scaffold(
      backgroundColor: eBg,
      appBar: AppBar(
        backgroundColor: eBg,
        elevation: 0,
        title: Text(
          '대화 기록',
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
            children: conversations.map((conv) {
              Color sentimentColor = conv['sentiment'] == '긍정적'
                  ? const Color(0xFF4CAF50)
                  : conv['sentiment'] == '부정적'
                      ? const Color(0xFFF44336)
                      : const Color(0xFFFFA726);

              return Container(
                margin: const EdgeInsets.only(bottom: 12),
                decoration: BoxDecoration(
                  color: eCard,
                  border: Border.all(color: eLine),
                  borderRadius: BorderRadius.circular(12),
                ),
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // 헤더
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              conv['elder'],
                              style: GoogleFonts.notoSansKr(
                                fontSize: 16,
                                fontWeight: FontWeight.w700,
                                color: eInk,
                              ),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              '${conv['date']} at ${conv['time']}',
                              style: const TextStyle(
                                fontSize: 12,
                                color: eInkSoft,
                              ),
                            ),
                          ],
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 10,
                            vertical: 6,
                          ),
                          decoration: BoxDecoration(
                            color: sentimentColor.withOpacity(0.15),
                            borderRadius: BorderRadius.circular(6),
                          ),
                          child: Text(
                            conv['sentiment'],
                            style: TextStyle(
                              fontSize: 11,
                              fontWeight: FontWeight.w600,
                              color: sentimentColor,
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),

                    // 컨텐츠
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: eBg,
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            '주제: ${conv['topic']}',
                            style: GoogleFonts.notoSansKr(
                              fontSize: 14,
                              fontWeight: FontWeight.w600,
                              color: eInk,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            '소요시간: ${conv['duration']}',
                            style: const TextStyle(
                              fontSize: 12,
                              color: eInkSoft,
                            ),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 12),

                    // 하단 버튼
                    Row(
                      children: [
                        Expanded(
                          child: OutlinedButton.icon(
                            onPressed: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (context) =>
                                      ConversationDetailScreen(
                                    date: conv['date'] as String,
                                    time: conv['time'] as String,
                                    elder: conv['elder'] as String,
                                    topic: conv['topic'] as String,
                                    sentiment: conv['sentiment'] as String,
                                    duration: conv['duration'] as String,
                                  ),
                                ),
                              );
                            },
                            icon: const Icon(Icons.visibility_outlined, size: 18),
                            label: const Text('상세보기'),
                            style: OutlinedButton.styleFrom(
                              foregroundColor: eAccent,
                              side: const BorderSide(color: eAccent),
                            ),
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: OutlinedButton.icon(
                            onPressed: () {
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(
                                  content: Text(
                                    '${conv['date']} ${conv['elder']}님과의 대화가 저장되었습니다.',
                                  ),
                                  backgroundColor: const Color(0xFF4CAF50),
                                  duration: const Duration(seconds: 2),
                                ),
                              );
                            },
                            icon: const Icon(Icons.download_outlined, size: 18),
                            label: const Text('내보내기'),
                            style: OutlinedButton.styleFrom(
                              foregroundColor: eAccent,
                              side: const BorderSide(color: eAccent),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              );
            }).toList(),
          ),
        ),
      ),
    );
  }
}
