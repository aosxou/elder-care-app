import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:fl_chart/fl_chart.dart';
import 'elderly_list_screen.dart';
import 'health_alerts_screen.dart';
import 'conversation_history_screen.dart';
import 'elderly_detail_screen.dart';
import 'guardian_account_screen.dart';

// 색상
const Color eBg = Color(0xFFFBF6ED);
const Color eBg2 = Color(0xFFF3E7D3);
const Color eCard = Color(0xFFFFFDF8);
const Color eInk = Color(0xFF3B2F26);
const Color eInkSoft = Color(0xFF93816D);
const Color eLine = Color(0xFFEADFC9);
const Color eAccent = Color(0xFFD97B4F);
const Color eAccentSoft = Color(0xFFFBE4D3);
const Color eBrass = Color(0xFFB78A4A);

class GuardianScreen extends StatefulWidget {
  const GuardianScreen({super.key});

  @override
  State<GuardianScreen> createState() => _GuardianScreenState();
}

class _GuardianScreenState extends State<GuardianScreen> {
  // Mock 어르신 데이터
  final List<Map<String, dynamic>> _elders = [
    {
      'id': 1,
      'name': '김할머니',
      'age': 78,
      'hasTalked': true,
      'status': '정상',
      'lastTalk': '오늘 14:30',
      'monthCalls': 23,
      'weekEmotion': 3.8,
      'alertCount': 2,
    },
    {
      'id': 2,
      'name': '이할아버지',
      'age': 82,
      'hasTalked': false,
      'status': '주의',
      'lastTalk': '어제 16:45',
      'monthCalls': 18,
      'weekEmotion': 3.2,
      'alertCount': 1,
    },
  ];

  // Mock 감정 추이 데이터 (최근 7일)
  final List<FlSpot> _emotionData = [
    FlSpot(0, 3.2),
    FlSpot(1, 3.5),
    FlSpot(2, 3.8),
    FlSpot(3, 3.6),
    FlSpot(4, 3.9),
    FlSpot(5, 4.1),
    FlSpot(6, 3.8),
  ];

  // Mock 알림 데이터
  final List<Map<String, dynamic>> _alerts = [
    {
      'type': '우울의심',
      'elder': '김할머니',
      'time': '오늘 14:30',
      'severity': 'high',
    },
    {
      'type': '미응답',
      'elder': '이할아버지',
      'time': '어제 09:00',
      'severity': 'medium',
    },
    {
      'type': '정상완료',
      'elder': '김할머니',
      'time': '어제 15:20',
      'severity': 'low',
    },
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: eBg,
      body: SafeArea(
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // 헤더 (인사말 + 설정 버튼)
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      '보호자님\n안녕하세요',
                      style: GoogleFonts.notoSerifKr(
                        fontSize: 28,
                        fontWeight: FontWeight.w700,
                        color: eInk,
                        height: 1.3,
                      ),
                    ),
                    GestureDetector(
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) =>
                                const GuardianAccountScreen(),
                          ),
                        );
                      },
                      child: Container(
                        width: 48,
                        height: 48,
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          color: eCard,
                          border: Border.all(color: eLine),
                        ),
                        child: const Icon(
                          Icons.settings_rounded,
                          color: eAccent,
                          size: 24,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 28),

                // 어르신 상태 카드들
                Text(
                  '관리 중인 어르신',
                  style: GoogleFonts.notoSerifKr(
                    fontSize: 16,
                    fontWeight: FontWeight.w700,
                    color: eInk,
                  ),
                ),
                const SizedBox(height: 12),
                ..._elders.map((elder) => _buildElderCard(context, elder)),
                const SizedBox(height: 32),

                // 요약 통계
                Text(
                  '이번 달 통계',
                  style: GoogleFonts.notoSerifKr(
                    fontSize: 16,
                    fontWeight: FontWeight.w700,
                    color: eInk,
                  ),
                ),
                const SizedBox(height: 12),
                Row(
                  children: [
                    Expanded(
                      child: _buildStatCard(
                        title: '통화 횟수',
                        value: '${_elders.fold<int>(0, (sum, e) => sum + (e['monthCalls'] as int))}회',
                        subtitle: '어르신 전체',
                        color: const Color(0xFF2196F3),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: _buildStatCard(
                        title: '평균 감정',
                        value: '3.8/5.0',
                        subtitle: '주간 평균',
                        color: const Color(0xFF4CAF50),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: _buildStatCard(
                        title: '확인 필요',
                        value: '${_alerts.length}건',
                        subtitle: '미응답 알림',
                        color: const Color(0xFFFFA726),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 32),

                // 감정 추이 그래프
                Text(
                  '최근 7일 감정 추이',
                  style: GoogleFonts.notoSerifKr(
                    fontSize: 16,
                    fontWeight: FontWeight.w700,
                    color: eInk,
                  ),
                ),
                const SizedBox(height: 12),
                _buildEmotionChart(),
                const SizedBox(height: 32),

                // 최근 알림 미리보기
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      '최근 알림',
                      style: GoogleFonts.notoSerifKr(
                        fontSize: 16,
                        fontWeight: FontWeight.w700,
                        color: eInk,
                      ),
                    ),
                    GestureDetector(
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => const HealthAlertsScreen(),
                          ),
                        );
                      },
                      child: Text(
                        '전체보기',
                        style: GoogleFonts.notoSansKr(
                          fontSize: 12,
                          fontWeight: FontWeight.w600,
                          color: eAccent,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 12),
                ..._alerts.take(2).map((alert) => _buildAlertItem(alert)),
                const SizedBox(height: 20),

                // 빠른 메뉴
                Row(
                  children: [
                    Expanded(
                      child: _buildQuickMenu(
                        title: '어르신 관리',
                        icon: Icons.people_outline_rounded,
                        onTap: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => const ElderlyListScreen(),
                            ),
                          );
                        },
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: _buildQuickMenu(
                        title: '대화 기록',
                        icon: Icons.chat_bubble_outline_rounded,
                        onTap: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) =>
                                  const ConversationHistoryScreen(),
                            ),
                          );
                        },
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildElderCard(BuildContext context, Map<String, dynamic> elder) {
    return GestureDetector(
      onTap: () {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => ElderlyDetailScreen(
              elderlyId: elder['id'],
              elderlyName: elder['name'],
            ),
          ),
        );
      },
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        decoration: BoxDecoration(
          color: eCard,
          border: Border.all(color: eLine),
          borderRadius: BorderRadius.circular(12),
        ),
        padding: const EdgeInsets.all(14),
        child: Row(
          children: [
            Container(
              width: 52,
              height: 52,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: elder['hasTalked']
                    ? const Color(0xFF4CAF50).withOpacity(0.1)
                    : const Color(0xFFFFA726).withOpacity(0.1),
              ),
              child: Icon(
                Icons.person_rounded,
                color: elder['hasTalked']
                    ? const Color(0xFF4CAF50)
                    : const Color(0xFFFFA726),
                size: 24,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Text(
                        elder['name'],
                        style: GoogleFonts.notoSansKr(
                          fontSize: 15,
                          fontWeight: FontWeight.w700,
                          color: eInk,
                        ),
                      ),
                      const SizedBox(width: 8),
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 6,
                          vertical: 2,
                        ),
                        decoration: BoxDecoration(
                          color: elder['hasTalked']
                              ? const Color(0xFF4CAF50)
                              : const Color(0xFFFFA726),
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: Text(
                          elder['hasTalked'] ? '완료' : '미완료',
                          style: const TextStyle(
                            fontSize: 10,
                            fontWeight: FontWeight.w600,
                            color: Colors.white,
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Text(
                    '마지막 통화: ${elder['lastTalk']}',
                    style: const TextStyle(
                      fontSize: 12,
                      color: eInkSoft,
                    ),
                  ),
                ],
              ),
            ),
            Icon(
              Icons.chevron_right_rounded,
              color: eInkSoft,
            ),
          ],
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
      padding: const EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: const TextStyle(
              fontSize: 11,
              color: eInkSoft,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            value,
            style: GoogleFonts.notoSansKr(
              fontSize: 18,
              fontWeight: FontWeight.w700,
              color: color,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            subtitle,
            style: const TextStyle(
              fontSize: 11,
              color: eInkSoft,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildEmotionChart() {
    return Container(
      decoration: BoxDecoration(
        color: eCard,
        border: Border.all(color: eLine),
        borderRadius: BorderRadius.circular(12),
      ),
      padding: const EdgeInsets.all(16),
      height: 200,
      child: BarChart(
        BarChartData(
          alignment: BarChartAlignment.spaceAround,
          maxY: 5,
          barTouchData: BarTouchData(enabled: false),
          titlesData: FlTitlesData(
            show: true,
            topTitles:
                AxisTitles(sideTitles: SideTitles(showTitles: false)),
            rightTitles:
                AxisTitles(sideTitles: SideTitles(showTitles: false)),
            leftTitles: AxisTitles(
              sideTitles: SideTitles(
                showTitles: true,
                getTitlesWidget: (value, meta) {
                  return Text(
                    '${value.toInt()}',
                    style: const TextStyle(
                      color: eInkSoft,
                      fontWeight: FontWeight.w500,
                      fontSize: 10,
                    ),
                  );
                },
              ),
            ),
            bottomTitles: AxisTitles(
              sideTitles: SideTitles(
                showTitles: true,
                getTitlesWidget: (value, meta) {
                  const days = ['월', '화', '수', '목', '금', '토', '일'];
                  return Text(
                    days[value.toInt()],
                    style: const TextStyle(
                      color: eInkSoft,
                      fontWeight: FontWeight.w500,
                      fontSize: 10,
                    ),
                  );
                },
              ),
            ),
          ),
          gridData: FlGridData(
            show: true,
            drawVerticalLine: false,
            getDrawingHorizontalGridLine: (value) {
              return FlLine(
                color: eLine.withOpacity(0.3),
                strokeWidth: 0.5,
              );
            },
          ),
          borderData: FlBorderData(show: false),
          barGroups: List.generate(
            _emotionData.length,
            (index) => BarChartGroupData(
              x: index,
              barRods: [
                BarChartRodData(
                  toY: _emotionData[index].y,
                  color: eAccent,
                  borderRadius: const BorderRadius.only(
                    topLeft: Radius.circular(4),
                    topRight: Radius.circular(4),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildAlertItem(Map<String, dynamic> alert) {
    Color typeColor = alert['severity'] == 'high'
        ? const Color(0xFFF44336)
        : alert['severity'] == 'medium'
            ? const Color(0xFFFFA726)
            : const Color(0xFF4CAF50);

    String typeLabel = alert['type'];

    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: typeColor.withOpacity(0.08),
        border: Border.all(color: typeColor.withOpacity(0.2)),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Row(
        children: [
          Container(
            width: 40,
            height: 40,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: typeColor.withOpacity(0.15),
            ),
            child: Icon(
              alert['severity'] == 'high'
                  ? Icons.warning_rounded
                  : Icons.notifications_rounded,
              color: typeColor,
              size: 20,
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '${alert['elder']} - $typeLabel',
                  style: GoogleFonts.notoSansKr(
                    fontSize: 13,
                    fontWeight: FontWeight.w600,
                    color: eInk,
                  ),
                ),
                const SizedBox(height: 2),
                Text(
                  alert['time'],
                  style: const TextStyle(
                    fontSize: 11,
                    color: eInkSoft,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildQuickMenu({
    required String title,
    required IconData icon,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        decoration: BoxDecoration(
          color: eCard,
          border: Border.all(color: eLine),
          borderRadius: BorderRadius.circular(12),
        ),
        padding: const EdgeInsets.symmetric(vertical: 16),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              icon,
              color: eAccent,
              size: 28,
            ),
            const SizedBox(height: 8),
            Text(
              title,
              style: GoogleFonts.notoSansKr(
                fontSize: 12,
                fontWeight: FontWeight.w600,
                color: eInk,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
