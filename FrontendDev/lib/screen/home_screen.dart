import 'package:flutter/material.dart';

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
        title: const Text(
          '홈',
          style: TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.w700,
          ),
        ),
        backgroundColor: Color(0xfffca63d),
      ),
      body: const Center(
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
