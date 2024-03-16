import 'package:barrier_free/screen/mypage/mypage_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:url_launcher/url_launcher.dart';

import 'common/bottomBar.dart';
import 'screen/home_screen.dart';
import 'screen/map/map_screen_temp.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await dotenv.load(fileName: ".env");
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  int _selectedIndex = 0;

  // 화면 리스트
  final List<Widget> _screens = [
    MapScreen(),
    HomeScreen(),
    MyPageScreen(),
  ];

  void _onItemTapped(int index) {
    if (index == 2) {
      _makePhoneCall('15881668');
    } else {
      if (index < 2) {
        setState(() {
          _selectedIndex = index;
        });
      } else if (index > 2) {
        setState(() {
          _selectedIndex = 2;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        appBarTheme: AppBarTheme(
            centerTitle: true,
            iconTheme: IconThemeData(
              color: Color(0xfffca63d),
            ),
            titleTextStyle: TextStyle(
                color: Color(0xfffca63d),
                fontWeight: FontWeight.bold,
                fontSize: 22.0)),
      ),
      home: Scaffold(
        body: Center(
          child: _screens.elementAt(_selectedIndex), // 현재 선택된 화면을 표시
        ),
        bottomNavigationBar: CustomBottomNavigationBar(
          selectedIndex: _selectedIndex,
          onItemSelected: _onItemTapped,
        ),
      ),
    );
  }
}

void _makePhoneCall(String phoneNumber) async {
  final Uri launchUri = Uri(
    scheme: 'tel',
    path: phoneNumber,
  );
  if (await canLaunchUrl(launchUri)) {
    await launchUrl(launchUri);
  } else {
    throw '$launchUri를 설치하지 못했습니다.';
  }
}
