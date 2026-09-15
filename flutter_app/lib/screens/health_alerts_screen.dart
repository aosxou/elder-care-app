import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

const Color eBg = Color(0xFFFBF6ED);
const Color eCard = Color(0xFFFFFDF8);
const Color eInk = Color(0xFF3B2F26);
const Color eInkSoft = Color(0xFF93816D);
const Color eLine = Color(0xFFEADFC9);
const Color eAccent = Color(0xFFD97B4F);

class MedicationAlert {
  final String name;
  final String time;
  final String dosage;
  final String nextAlert;
  final bool notified;

  MedicationAlert({
    required this.name,
    required this.time,
    required this.dosage,
    required this.nextAlert,
    required this.notified,
  });
}

class EmergencyAlert {
  final String type;
  final String title;
  final String message;
  final String time;
  final String severity;

  EmergencyAlert({
    required this.type,
    required this.title,
    required this.message,
    required this.time,
    required this.severity,
  });
}

class HealthCheck {
  final String date;
  final String time;
  final String elder;
  final String duration;
  final String status;
  final String notes;

  HealthCheck({
    required this.date,
    required this.time,
    required this.elder,
    required this.duration,
    required this.status,
    required this.notes,
  });
}

class HealthAlertsScreen extends StatefulWidget {
  const HealthAlertsScreen({super.key});

  @override
  State<HealthAlertsScreen> createState() => _HealthAlertsScreenState();
}

class _HealthAlertsScreenState extends State<HealthAlertsScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: eBg,
      appBar: AppBar(
        backgroundColor: eBg,
        elevation: 0,
        title: Text(
          '건강 알림',
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
        bottom: TabBar(
          controller: _tabController,
          labelColor: eAccent,
          unselectedLabelColor: eInkSoft,
          indicatorColor: eAccent,
          labelStyle: GoogleFonts.notoSansKr(
            fontWeight: FontWeight.w700,
          ),
          tabs: const [
            Tab(text: '약물 복용'),
            Tab(text: '긴급 알림'),
            Tab(text: '건강 확인'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildMedicationTab(),
          _buildEmergencyTab(),
          _buildHealthCheckTab(),
        ],
      ),
    );
  }

  Widget _buildMedicationTab() {
    final medications = <MedicationAlert>[
      MedicationAlert(
        name: '혈압약',
        time: '09:00',
        dosage: '1일 1회',
        nextAlert: '내일 09:00',
        notified: true,
      ),
      MedicationAlert(
        name: '당뇨약',
        time: '12:00',
        dosage: '1일 1회',
        nextAlert: '내일 12:00',
        notified: true,
      ),
      MedicationAlert(
        name: '수면제',
        time: '21:00',
        dosage: '1일 1회',
        nextAlert: '오늘 21:00',
        notified: false,
      ),
    ];

    return SingleChildScrollView(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              '약물 복용 일정 및 알림',
              style: GoogleFonts.notoSerifKr(
                fontSize: 16,
                fontWeight: FontWeight.w700,
                color: eInk,
              ),
            ),
            const SizedBox(height: 16),
            ...medications.map((med) {
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
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              med.name,
                              style: GoogleFonts.notoSansKr(
                                fontSize: 16,
                                fontWeight: FontWeight.w700,
                                color: eInk,
                              ),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              '${med.time} (${med.dosage})',
                              style: const TextStyle(
                                fontSize: 13,
                                color: eInkSoft,
                              ),
                            ),
                          ],
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 12,
                            vertical: 6,
                          ),
                          decoration: BoxDecoration(
                            color: med.notified
                                ? const Color(0xFF4CAF50).withOpacity(0.15)
                                : eAccent.withOpacity(0.15),
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Text(
                            med.notified ? '알림 완료' : '알림 대기',
                            style: TextStyle(
                              fontSize: 12,
                              fontWeight: FontWeight.w600,
                              color: med.notified
                                  ? const Color(0xFF4CAF50)
                                  : eAccent,
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),
                    Row(
                      children: [
                        const Icon(Icons.notifications_active_rounded,
                            size: 16, color: eInkSoft),
                        const SizedBox(width: 8),
                        Text(
                          '다음 알림: ${med.nextAlert}',
                          style: const TextStyle(
                            fontSize: 12,
                            color: eInkSoft,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              );
            }).toList(),
            const SizedBox(height: 16),
            Container(
              decoration: BoxDecoration(
                color: eAccent.withOpacity(0.1),
                border: Border.all(color: eAccent.withOpacity(0.2)),
                borderRadius: BorderRadius.circular(12),
              ),
              padding: const EdgeInsets.all(14),
              child: Row(
                children: [
                  const Icon(Icons.info_outline_rounded,
                      color: eAccent, size: 20),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Text(
                      '보호자와 어르신 모두에게 약물 복용 시간에 푸시 알림이 전송됩니다.',
                      style: TextStyle(
                        fontSize: 12,
                        color: eInkSoft,
                        height: 1.5,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEmergencyTab() {
    final emergencies = <EmergencyAlert>[
      EmergencyAlert(
        type: '통화 연결 실패',
        title: '통화 연결 안됨',
        message: '김할머니와의 통화 연결이 실패했습니다.',
        time: '오늘 14:30',
        severity: 'high',
      ),
      EmergencyAlert(
        type: '우울도 급증',
        title: '우울 수치 증가',
        message: '최근 우울 수치가 급격히 증가했습니다. 주의 필요합니다.',
        time: '어제 16:45',
        severity: 'high',
      ),
      EmergencyAlert(
        type: '건강 이상',
        title: '비정상 수치 감지',
        message: '혈압이 평상시보다 높게 측정되었습니다.',
        time: '어제 11:20',
        severity: 'medium',
      ),
    ];

    return SingleChildScrollView(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: emergencies.map((alert) {
            Color severityColor = alert.severity == 'high'
                ? const Color(0xFFF44336)
                : const Color(0xFFFFA726);

            return Container(
              margin: const EdgeInsets.only(bottom: 12),
              decoration: BoxDecoration(
                color: eCard,
                border: Border.all(
                  color: severityColor.withOpacity(0.3),
                  width: 1.5,
                ),
                borderRadius: BorderRadius.circular(12),
              ),
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        alert.title,
                        style: GoogleFonts.notoSansKr(
                          fontSize: 16,
                          fontWeight: FontWeight.w700,
                          color: eInk,
                        ),
                      ),
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 10,
                          vertical: 4,
                        ),
                        decoration: BoxDecoration(
                          color: severityColor.withOpacity(0.15),
                          borderRadius: BorderRadius.circular(6),
                        ),
                        child: Text(
                          alert.type,
                          style: TextStyle(
                            fontSize: 11,
                            fontWeight: FontWeight.w600,
                            color: severityColor,
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Text(
                    alert.message,
                    style: const TextStyle(
                      fontSize: 13,
                      color: eInkSoft,
                      height: 1.5,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    alert.time,
                    style: const TextStyle(
                      fontSize: 12,
                      color: eInkSoft,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ],
              ),
            );
          }).toList(),
        ),
      ),
    );
  }

  Widget _buildHealthCheckTab() {
    final healthChecks = <HealthCheck>[
      HealthCheck(
        date: '2024-12-15',
        time: '15:30',
        elder: '김할머니',
        duration: '15분',
        status: '정상',
        notes: '통화 종료 후 건강 상태 확인 완료. 특이사항 없음.',
      ),
      HealthCheck(
        date: '2024-12-14',
        time: '14:15',
        elder: '이할아버지',
        duration: '10분',
        status: '정상',
        notes: '건강 상태 점검. 모든 지표 정상 범위.',
      ),
      HealthCheck(
        date: '2024-12-13',
        time: '16:45',
        elder: '김할머니',
        duration: '25분',
        status: '정상',
        notes: '상세 건강 확인 완료. 긍정적인 감정 상태 유지.',
      ),
    ];

    return SingleChildScrollView(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: healthChecks.map((check) {
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
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            check.elder,
                            style: GoogleFonts.notoSansKr(
                              fontSize: 16,
                              fontWeight: FontWeight.w700,
                              color: eInk,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            '${check.date} ${check.time}',
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
                          color: const Color(0xFF4CAF50).withOpacity(0.15),
                          borderRadius: BorderRadius.circular(6),
                        ),
                        child: Text(
                          check.status,
                          style: const TextStyle(
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                            color: Color(0xFF4CAF50),
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: eBg,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          children: [
                            const Icon(Icons.phone_rounded,
                                size: 16, color: eAccent),
                            const SizedBox(width: 8),
                            Text(
                              '통화 시간: ${check.duration}',
                              style: const TextStyle(
                                fontSize: 12,
                                color: eInkSoft,
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 8),
                        Text(
                          check.notes,
                          style: const TextStyle(
                            fontSize: 12,
                            color: eInkSoft,
                            height: 1.5,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            );
          }).toList(),
        ),
      ),
    );
  }
}
