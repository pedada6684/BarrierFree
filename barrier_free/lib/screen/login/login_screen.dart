import 'dart:convert';

import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/provider/user_provider.dart';
import 'package:barrier_free/screen/login/login_platform.dart';
import 'package:barrier_free/screen/map/map_screen.dart';
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
  void signInWithNaver() async {
    final NaverLoginResult result = await FlutterNaverLogin.logIn();

    if (result.status == NaverLoginStatus.loggedIn) {
      // print('===================네이버로그인====================');
      // print('네이버 로그인 결과 ${result.account}');
      // print('===================네이버로그인====================');

      if (!mounted) return;
      //사용자 정보 받아오기
      final String? nickname = result.account.nickname;
      final String? email = result.account.email;
      final String? profileImageUrl = result.account.profileImage;
      final String? name = result.account.name;

      //userProvider에 사용자 정보
      if (!mounted) return;
      Provider.of<UserProvider>(context, listen: false)
          .setUser(nickname, email!, profileImageUrl!, name!);

      // 백으로 정보 전달
      final userId = await sendNaverLoginInfo(result.account);
      if (!mounted) return;
      //네이버 로그인
      Provider.of<UserProvider>(context, listen: false)
          .signIn(LoginPlatform.naver, userId);
    }

    onLoginSuccess();
  }

  Future<int> sendNaverLoginInfo(NaverAccountResult account) async {
    // final uri = Uri.parse("https://hbaf.site/api/member/naverLogin");
    final uri = Uri.parse('https://hbaf.site/api/v1/auth/appLogin');
    // final uri = Uri.parse('https://hbaf.site/api/v1/auth/test');
    //토큰
    try {
      final response = await http.post(uri, body: {
        'nickname': account.nickname,
        'name': account.name,
        'email': account.email,
        'profileImage': account.profileImage,
      });
      print(response.headers);

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final accessToken = data['accessToken'];
        final cookies = response.headers['set-cookie'];

        final secureStorageService = SecureStorageService();

        print('백에서 넘어 온 쿠키, 토큰 데이터 : $data');
        return await secureStorageService.saveToken(accessToken, cookies!);
      } else {
        throw Exception('${response.statusCode}');
      }
    } catch (e) {
      throw Exception('오류발생 오류발생 $e');
    }
  }

  void signOut() async {
    await FlutterNaverLogin.logOut();

    Provider.of<UserProvider>(context, listen: false).signOut();
  }

  void onLoginSuccess() async {
    await Navigator.of(context).pushAndRemoveUntil(MaterialPageRoute(builder: (context)=>MyPageScreen()), (route) => false);
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
