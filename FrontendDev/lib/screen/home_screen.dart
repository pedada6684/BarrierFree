import 'package:barrier_free/screen/bottomBar.dart';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import 'map/map_screen_temp.dart';

class HomeScreen extends StatefulWidget {

  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        title: Text(
          '홈',
          style: TextStyle(
            color: Colors.white,
          ),
        ),
        backgroundColor: Color(0xfffca63d),
      ),
      body: Center(
        child: Text('홈화면'),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center, // 중앙 정렬
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  "함께 가는 길, 장애물 없는 하루", // 추가할 텍스트
                  style: TextStyle(
                    color: Color(0xfffca63d),
                    fontSize: 22.0,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Text(
                  "베프.",
                  style: TextStyle(
                    color: Color(0xfffca63d),
                    fontFamily: 'LogoFont',
                    fontSize: 100.0,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
