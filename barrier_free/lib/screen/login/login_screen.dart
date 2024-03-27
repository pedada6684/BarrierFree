import 'dart:convert';

import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/provider/user_provider.dart';
import 'package:barrier_free/screen/login/login_platform.dart';
import 'package:barrier_free/screen/map/map_screen.dart';
import 'package:barrier_free/services/secure_storage_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_naver_login/flutter_naver_login.dart';
import 'package:http/http.dart' as http;
import 'package:provider/provider.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  // LoginPlatform _loginPlatform = LoginPlatform.none;
  int _selectedIndex = 0;

  void signInWithNaver() async {
    final NaverLoginResult result = await FlutterNaverLogin.logIn();

    if (result.status == NaverLoginStatus.loggedIn) {
      print('=======================================');
      print(result.accessToken);
      print(result.account);
      print('=======================================');

      //사용자 정보 받아오기
      final String? nickname = result.account.nickname;
      final String? email = result.account.email;
      final String? profileImageUrl = result.account.profileImage;
      final String? name = result.account.name;

      //userProvider에 사용자 정보
      Provider.of<UserProvider>(context, listen: false)
          .setUser(nickname, email!, profileImageUrl!, name!);

      //백으로 정보 전달
      final response = await sendNaverLoginInfo(result.account);
      if (response != null) {
        //토큰 저장 by secure storage
        final secureStorageService = SecureStorageService();
        await secureStorageService.saveToken(response);
      }

      //네이버 로그인
      Provider.of<UserProvider>(context, listen: false)
          .signIn(LoginPlatform.naver);
    }

    setState(() {
      _selectedIndex = 0;
    });

    onLoginSuccess();
  }

  Future<String?> sendNaverLoginInfo(NaverAccountResult account) async {
    final uri = Uri.parse("https://hbaf.site/api/member/naverLogin");
    //토큰
    final response = await http.post(uri, body: {
      'nickname': account.nickname,
      'name': account.name,
      'email': account.email,
      'profileImage': account.profileImage,
    });

    print(response.body);

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      print(data);
    }
  }

  void signOut() async {
    await FlutterNaverLogin.logOut();

    Provider.of<UserProvider>(context, listen: false).signOut();
  }

  void onLoginSuccess() async {
    await showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('로그인 성공'),
        content: Text('장애물 없는 하루를 시작합니다.'),
        actions: <Widget>[
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
            },
            child: Text('확인'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Column(
                children: [
                  Row(
                    children: [
                      SizedBox(
                        width: 60.0,
                      ),
                      Text(
                        '함께가는 길, 장애물 없는 하루',
                        style: TextStyle(
                          color: mainOrange,
                          fontSize: 22.0,
                          fontWeight: FontWeight.bold,
                          height: 1,
                        ),
                      ),
                    ],
                  ),
                  Row(
                    children: [
                      SizedBox(
                        width: 60.0,
                      ),
                      Text(
                        '베프.',
                        textAlign: TextAlign.center,
                        style: TextStyle(
                          fontFamily: 'LogoFont',
                          fontSize: 100.0,
                          color: mainOrange,
                          height: 1,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ],
          ),
          SizedBox(
            height: 80.0,
          ),
          _loginButton('naver_login', signInWithNaver),
        ],
      ),
    );
  }

  Widget _loginButton(String path, VoidCallback onTap) {
    return Ink(
      width: 225.0,
      height: 60.0,
      decoration: BoxDecoration(
        image: DecorationImage(
          image: AssetImage('assets/image/$path.png'),
          fit: BoxFit.cover,
        ),
      ),
      child: InkWell(
        onTap: onTap,
      ),
    );
  }
}
