import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'elderly_detail_screen.dart';

const Color eBg = Color(0xFFFBF6ED);
const Color eCard = Color(0xFFFFFDF8);
const Color eInk = Color(0xFF3B2F26);
const Color eInkSoft = Color(0xFF93816D);
const Color eLine = Color(0xFFEADFC9);
const Color eAccent = Color(0xFFD97B4F);

class ElderlyListScreen extends StatelessWidget {
  const ElderlyListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // 더미 데이터
    final List<Map<String, dynamic>> elderlies = [
      {
        'id': 1,
        'name': '김할머니',
        'age': 78,
        'status': '정상',
        'statusColor': Color(0xFF4CAF50),
      },
      {
        'id': 2,
        'name': '이할아버지',
        'age': 82,
        'status': '주의',
        'statusColor': Color(0xFFFFA726),
      },
    ];

    return Scaffold(
      backgroundColor: eBg,
      appBar: AppBar(
        backgroundColor: eBg,
        elevation: 0,
        title: Text(
          '관리 중인 어르신',
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
            children: elderlies.map((elderly) {
              return GestureDetector(
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => ElderlyDetailScreen(
                        elderlyId: elderly['id'],
                        elderlyName: elderly['name'],
                      ),
                    ),
                  );
                },
                child: Container(
                  margin: const EdgeInsets.only(bottom: 16),
                  decoration: BoxDecoration(
                    color: eCard,
                    border: Border.all(color: eLine, width: 1),
                    borderRadius: BorderRadius.circular(16),
                  ),
                  padding: const EdgeInsets.all(20),
                  child: Row(
                    children: [
                      // 프로필
                      Container(
                        width: 60,
                        height: 60,
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          color: eAccent.withOpacity(0.2),
                        ),
                        child: const Icon(
                          Icons.person_outline_rounded,
                          size: 32,
                          color: eAccent,
                        ),
                      ),
                      const SizedBox(width: 16),
                      // 정보
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              elderly['name'],
                              style: GoogleFonts.notoSansKr(
                                fontSize: 18,
                                fontWeight: FontWeight.w700,
                                color: eInk,
                              ),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              '${elderly['age']}세',
                              style: TextStyle(
                                fontSize: 14,
                                color: eInkSoft,
                              ),
                            ),
                          ],
                        ),
                      ),
                      // 상태
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 12,
                          vertical: 6,
                        ),
                        decoration: BoxDecoration(
                          color: elderly['statusColor'].withOpacity(0.15),
                          borderRadius: BorderRadius.circular(8),
                          border: Border.all(
                            color: elderly['statusColor'].withOpacity(0.3),
                          ),
                        ),
                        child: Text(
                          elderly['status'],
                          style: TextStyle(
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                            color: elderly['statusColor'],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              );
            }).toList(),
          ),
        ),
      ),
    );
  }
}
