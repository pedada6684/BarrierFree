import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/provider/user_provider.dart';
import 'package:barrier_free/screen/login/login_screen.dart';
import 'package:barrier_free/screen/map/map_screen.dart';
import 'package:barrier_free/screen/mypage/myplace_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_naver_login/flutter_naver_login.dart';
import 'package:provider/provider.dart';

import 'myfavorite_screen.dart';
import 'myreview_screen.dart';

class MyPageScreen extends StatefulWidget {
  const MyPageScreen({super.key});

  @override
  State<MyPageScreen> createState() => _MyPageScreenState();
}

class _MyPageScreenState extends State<MyPageScreen> {

  final Map<String, Widget Function(BuildContext)> menuItems = {
    '즐겨찾기': (context) => const MyFavoriteScreen(),
    '게시글': (context) => const MyReviewScreen(),
  };

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: '마이페이지'),
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
    var userProvider = Provider.of<UserProvider>(context);
    //로그인 여부 체크하기
    var isLoggedIn = userProvider.isLoggedIn();

    if (isLoggedIn) {
      var nickname =
          userProvider.nickname ?? userProvider.name; //닉네임 없을 때 이름으로 보여주기
      var profileImageUrl = userProvider.profileImage;

      return Padding(
        padding: EdgeInsets.all(8.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            //유저 닉네임마다 달라질 것
            Text(
              '$nickname 님,\n환영합니다.',
              style: TextStyle(
                fontSize: 20.0,
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
            ),
            CircleAvatar(
              radius: 50,
              backgroundColor: Colors.white,
              backgroundImage:
                  profileImageUrl != null && profileImageUrl is String
                      ? NetworkImage(profileImageUrl)
                      : const AssetImage('assets/image/default_profile.png')
                          as ImageProvider,
            ),
          ],
        ),
      );
    } else {
      return Center(
        child: Text(
          '로그인이 필요합니다.',
          style: TextStyle(
            fontSize: 20.0,
            fontWeight: FontWeight.bold,
            color: Colors.white,
          ),
        ),
      );
    }
  }

  Widget _buildEditProfileButton() {
    var userProvider = Provider.of<UserProvider>(context);
    var isLoggedIn = userProvider.isLoggedIn();

    if (isLoggedIn) {
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
    } else {
      return ElevatedButton(
        onPressed: () {
          Navigator.push(
              context, MaterialPageRoute(builder: (context) => LoginScreen()));
        },
        child: Text(
          '로그인',
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
  }

  List<Widget> _buildMenuItems(
      Map<String, Widget Function(BuildContext)> menuItems) {
    var userProvider = Provider.of<UserProvider>(context);
    var isLoggedIn = userProvider.isLoggedIn();

    if (isLoggedIn) {
      return [
        ...menuItems.entries.map((entry) {
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
                Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => entry.value(context)));
              },
            ),
          );
        }).toList(),
        Padding(
          padding: const EdgeInsets.all(8.0),
          child: ListTile(
            leading: const Text(
              '로그아웃',
              style: TextStyle(
                fontSize: 18.0,
              ),
            ),
            trailing: const Icon(
              Icons.arrow_forward_ios,
              color: mainGray,
            ),
            onTap: () async {
              await FlutterNaverLogin.logOut();
              await Provider.of<UserProvider>(context, listen: false).signOut();
              Navigator.push(context,
                  MaterialPageRoute(builder: (context) => MapScreen()));
            },
          ),
        ),
      ];
    } else {
      // 로그인되지 않은 상태에서는 메뉴 비우기
      return [];
    }
  }
}
