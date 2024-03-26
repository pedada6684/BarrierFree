import 'dart:convert';

import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/provider/user_provider.dart';
import 'package:barrier_free/screen/login/login_platform.dart';
import 'package:barrier_free/screen/mypage/mypage_screen.dart';
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
  LoginPlatform _loginPlatform = LoginPlatform.none;

  void signInWithNaver() async {
    final NaverLoginResult result = await FlutterNaverLogin.logIn();

    if (result.status == NaverLoginStatus.loggedIn) {
      print('=======================================');
      print(result.accessToken);
      print(result.account);
      print('=======================================');

      //백으로 정보 전달
      final response = await sendNaverLoginInfo(result.account);
      if (response != null) {
        //토큰 저장 by secure storage
        final secureStorageService = SecureStorageService();
        await secureStorageService.saveToken(response);
      }

      setState(() {
        //네이버로 로그인
        _loginPlatform = LoginPlatform.naver;
      });

      //네이버 로그인
      Provider.of<UserProvider>(context, listen: false)
          .signIn(LoginPlatform.naver);

    }
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
    switch (_loginPlatform) {
      case LoginPlatform.naver:
        await FlutterNaverLogin.logOut();
        break;
      case LoginPlatform.none:
        break;
    }
    setState(() {
      _loginPlatform = LoginPlatform.none;
    });

    Provider.of<UserProvider>(context, listen: false).signOut();

  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: mainOrange,
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    '함께가는 길, 장애물 없는 하루',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 22.0,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  Text(
                    '베프.',
                    textAlign: TextAlign.center,
                    style: TextStyle(
                        fontFamily: 'LogoFont',
                        fontSize: 100.0,
                        color: Colors.white),
                  ),
                ],
              ),
            ],
          ),
          const SizedBox(
            height: 100.0,
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
