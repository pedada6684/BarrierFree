import 'package:flutter/material.dart';

import 'screen/bottomBar.dart';
import 'screen/home_screen.dart';
import 'screen/map/map_screen_temp.dart';

void main() {
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
      home: Scaffold(
        // appBar: AppBar(
        //   title: Text('앱 타이틀'),
        //   backgroundColor: Color(0xfffca63d),
        // ),
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
