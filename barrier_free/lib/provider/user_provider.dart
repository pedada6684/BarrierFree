import 'package:barrier_free/screen/login/login_platform.dart';
import 'package:barrier_free/services/secure_storage_service.dart';
import 'package:flutter/material.dart';

class UserProvider with ChangeNotifier {
  //로그인
  LoginPlatform _loginPlatform = LoginPlatform.none;

  //로그인 상태 접근
  LoginPlatform get loginPlatform => _loginPlatform;

  //로그인
  void signIn(LoginPlatform platform) {
    _loginPlatform = platform;
    notifyListeners();
  }

  //로그아웃
  Future<void> signOut() async {
    _loginPlatform = LoginPlatform.none;
    notifyListeners();
    final secureStorageService = SecureStorageService();
    //토큰 삭제!!
    await secureStorageService.deleteToken();
  }

  bool isLoggedIn() => _loginPlatform != LoginPlatform.none;
}
