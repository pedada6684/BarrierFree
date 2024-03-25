import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/provider/user_provider.dart';
import 'package:barrier_free/screen/login/login_platform.dart';
import 'package:flutter/material.dart';
import 'package:flutter_naver_login/flutter_naver_login.dart';
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

      setState(() {
        //네이버로 로그인
        _loginPlatform = LoginPlatform.naver;
      });

      Provider.of<UserProvider>(context, listen: false)
          .signIn(LoginPlatform.naver);
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
          Column(
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
          SizedBox(
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
