import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/screen/mypage/myplace_screen.dart';
import 'package:flutter/material.dart';

import 'myfavorite_screen.dart';
import 'myreview_screen.dart';

class MyPageScreen extends StatefulWidget {
  const MyPageScreen({super.key});

  @override
  State<MyPageScreen> createState() => _MyPageScreenState();
}

class _MyPageScreenState extends State<MyPageScreen> {
  final Map<String, Widget Function(BuildContext)> menuItems = {
    '내 장소': (context) => MyPlaceScreen(),
    '즐겨찾기': (context) => MyFavoriteScreen(),
    '게시글': (context) => MyReviewScreen(),
  };

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(title: '마이페이지'),
      body: ListView(
        children: [
          _buildTopSection(),
          ..._buildMenuItems(menuItems),
        ],
      ),
    );
  }

  Widget _buildTopSection() {
    return Container(
      color: mainOrange,
      height: 230.0,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          _buildUserInfo(),
          _buildEditProfileButton(),
        ],
      ),
    );
  }

  Widget _buildUserInfo() {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: const [
          //유저 닉네임마다 달라질 것
          Text(
            '배프 님,\n환영합니다.',
            style: TextStyle(
              fontSize: 20.0,
              fontWeight: FontWeight.bold,
              color: Colors.white,
            ),
          ),
          CircleAvatar(
            radius: 50,
            backgroundColor: Colors.white,
          ),
        ],
      ),
    );
  }

  Widget _buildEditProfileButton() {
    return ElevatedButton(
      onPressed: () {},
      child: const Text(
        '프로필 수정하기',
        style: TextStyle(
          color: mainOrange,
          fontWeight: FontWeight.bold,
          fontSize: 18.0,
        ),
      ),
      style: ElevatedButton.styleFrom(
        backgroundColor: Colors.white,
        fixedSize: const Size(300.0, 25.0),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8.0),
        ),
      ),
    );
  }

  List<Widget> _buildMenuItems(
      Map<String, Widget Function(BuildContext)> menuItems) {
    return menuItems.entries
        .map((MapEntry<String, Widget Function(BuildContext)> entry) {
      return Padding(
        padding: const EdgeInsets.all(8.0),
        child: ListTile(
          leading: Text(
            entry.key,
            style: const TextStyle(
              fontSize: 18.0,
            ),
          ),
          trailing: const Icon(
            Icons.arrow_forward_ios,
            color: mainGray,
          ),
          onTap: () {
            Navigator.push(context, MaterialPageRoute(builder: entry.value));
          },
        ),
      );
    }).toList();
  }
}
