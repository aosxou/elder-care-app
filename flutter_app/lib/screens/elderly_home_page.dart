import 'package:flutter/material.dart';
import 'main_screen.dart';
import 'ai_chat_screen.dart';
import 'emergency_screen.dart';

const Color eCard = Color(0xFFFFFDF8);
const Color eInkSoft = Color(0xFF93816D);
const Color eAccent = Color(0xFFD97B4F);

class ElderlyHomePage extends StatefulWidget {
  const ElderlyHomePage({super.key});

  @override
  State<ElderlyHomePage> createState() => _ElderlyHomePageState();
}

class _ElderlyHomePageState extends State<ElderlyHomePage> {
  int _selectedIndex = 0;

  final List<Widget> _screens = [
    const MainScreen(),
    const AiChatScreen(),
    const EmergencyScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _screens[_selectedIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: (index) {
          setState(() {
            _selectedIndex = index;
          });
        },
        backgroundColor: eCard,
        selectedItemColor: eAccent,
        unselectedItemColor: eInkSoft,
        type: BottomNavigationBarType.fixed,
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.home_rounded),
            label: '홈',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.chat_bubble_outline_rounded),
            label: '채팅',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.shield_outlined),
            label: '비상연락',
          ),
        ],
      ),
    );
  }
}
