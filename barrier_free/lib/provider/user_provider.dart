import 'package:barrier_free/screen/login/login_platform.dart';
import 'package:flutter/material.dart';

class UserProvider with ChangeNotifier {
  LoginPlatform _loginPlatform = LoginPlatform.none;

  LoginPlatform get loginPlatform => _loginPlatform;

  //로그인
  void signIn(LoginPlatform platform){
    _loginPlatform = platform;
    notifyListeners();
  }

  //로그아웃
  void signOut(){
    _loginPlatform = LoginPlatform.none;
    notifyListeners();
  }
}
