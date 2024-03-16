import 'package:barrier_free/screen/mypage/mypage_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

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
    HomeScreen(),
    MapScreen(),
    MyPageScreen(),
  ];

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
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
