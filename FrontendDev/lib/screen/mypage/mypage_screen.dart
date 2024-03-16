import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class MyPageScreen extends StatefulWidget {
  const MyPageScreen({super.key});

  @override
  State<MyPageScreen> createState() => _MyPageScreenState();
}

class _MyPageScreenState extends State<MyPageScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        title: const Text(
          '마이페이지',
          style: TextStyle(
            color: Color(0xfffca63d),
            fontWeight: FontWeight.w600,
          ),
        ),
        backgroundColor: Colors.white,
      ),
      body: ListView(
        children: [
          _buildTopSection(),
          ..._buildMenuItems([
            '내 장소',
            '즐겨찾기',
            '게시글',
            '로그아웃',
            '탈퇴하기',
          ]),
        ],
      ),
    );
  }

  Widget _buildTopSection() {
    return Container(
      color: Color(0xfffca63d),
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
          color: Color(0xfffca63d),
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

  List<Widget> _buildMenuItems(List<String> menuItems) {
    return menuItems.map((item) {
      return Padding(
        padding: const EdgeInsets.all(8.0),
        child: ListTile(
          leading: Text(
            item,
            style: const TextStyle(
              fontSize: 20.0,
            ),
          ),
          trailing: const Icon(
            Icons.arrow_forward_ios,
            color: Color(0xff909090),
          ),
          onTap: () {},
        ),
      );
    }).toList();
  }
}
