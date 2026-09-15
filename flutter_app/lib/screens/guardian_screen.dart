import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'elderly_list_screen.dart';
import 'health_alerts_screen.dart';
import 'conversation_history_screen.dart';

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
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: eBg,
      body: SafeArea(
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 26, vertical: 46),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // 인사말
                Text(
                  '보호자님\n안녕하세요',
                  style: GoogleFonts.notoSerifKr(
                    fontSize: 27,
                    fontWeight: FontWeight.w700,
                    color: eInk,
                    height: 1.4,
                  ),
                ),
                const SizedBox(height: 38),

                // 관리 중인 어르신 카드
                _buildGuardianCard(
                  title: '관리 중인 어르신',
                  subtitle: '2명',
                  backgroundColor: Color(0xFFFEF5E7),
                  iconColor: eBrass,
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
                const SizedBox(height: 16),

                // 건강 알림 카드
                _buildGuardianCard(
                  title: '건강 알림',
                  subtitle: '새 알림 3개',
                  backgroundColor: eAccentSoft,
                  iconColor: eAccent,
                  icon: Icons.notifications_active_outlined,
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const HealthAlertsScreen(),
                      ),
                    );
                  },
                ),
                const SizedBox(height: 16),

                // 대화 기록 카드
                _buildGuardianCard(
                  title: '대화 기록',
                  subtitle: '최근 대화 조회',
                  backgroundColor: Color(0xFFF3E4D8),
                  iconColor: eBrass,
                  icon: Icons.chat_bubble_outline_rounded,
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const ConversationHistoryScreen(),
                      ),
                    );
                  },
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildGuardianCard({
    required String title,
    required String subtitle,
    required Color backgroundColor,
    required Color iconColor,
    required IconData icon,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        decoration: BoxDecoration(
          color: eCard,
          border: Border.all(color: eLine, width: 1),
          borderRadius: BorderRadius.circular(18),
        ),
        padding: const EdgeInsets.symmetric(horizontal: 22, vertical: 22),
        child: Row(
          children: [
            Container(
              width: 60,
              height: 60,
              decoration: BoxDecoration(
                color: backgroundColor,
                borderRadius: BorderRadius.circular(16),
              ),
              child: Icon(
                icon,
                color: iconColor,
                size: 28,
              ),
            ),
            const SizedBox(width: 18),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: GoogleFonts.notoSansKr(
                      fontSize: 18,
                      fontWeight: FontWeight.w800,
                      color: eInk,
                    ),
                  ),
                  const SizedBox(height: 3),
                  Text(
                    subtitle,
                    style: TextStyle(
                      fontSize: 14,
                      color: eInkSoft,
                    ),
                  ),
                ],
              ),
            ),
            Text(
              '›',
              style: TextStyle(
                fontSize: 24,
                color: eInkSoft,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
