import 'package:barrier_free/screen/login/login_platform.dart';
import 'package:barrier_free/services/secure_storage_service.dart';
import 'package:flutter/material.dart';

class UserProvider with ChangeNotifier {



  //유저 정보
  String? _nickname;
  String? _email;
  String? _profileImage;
  String? _name;

  //유저 정보 접근자
  String? get nickname => _nickname;

  String? get email => _email;

  String? get profileImage => _profileImage;

  String? get name => _name;

  //유저 정보 설정하기
  void setUser(
      String? nickname, String? email, String? profileImage, String? name) {
    print('====================Setting user info: $profileImage====================');
    _nickname = nickname;
    _email = email;
    _profileImage = profileImage;
    _name = name;
    notifyListeners();
  }

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
