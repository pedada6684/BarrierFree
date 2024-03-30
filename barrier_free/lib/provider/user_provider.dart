import 'package:barrier_free/screen/login/login_platform.dart';
import 'package:barrier_free/services/secure_storage_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_naver_login/flutter_naver_login.dart';

class UserProvider with ChangeNotifier {
  //유저 정보
  String? _nickname;
  String? _email;
  String? _profileImage;
  String? _name;
  int? _userId;

  //유저 정보 접근자
  String? get nickname => _nickname;

  String? get email => _email;

  String? get profileImage => _profileImage;

  String? get name => _name;

  int? get userId => _userId;

  //유저 정보 설정하기
  void setUser(
    String? nickname,
    String? email,
    String? profileImage,
    String? name,
  ) {
    print(
        '====================Setting user info: $profileImage====================');
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
  void signIn(LoginPlatform platform, int userId) {
    _loginPlatform = platform;
    setUserId(userId);
    notifyListeners();
  }

  //로그아웃
  Future<void> signOut() async {

      //네이버일경우
      await FlutterNaverLogin.logOutAndDeleteToken();

    _loginPlatform = LoginPlatform.none; //플랫폼 초기화\
    notifyListeners();

    final secureStorageService = SecureStorageService();
    //토큰 삭제!!
    await secureStorageService.deleteToken();
  }

  void setUserId(int userId) {
    _userId = userId;
    print('userId 세팅 완료');
    notifyListeners();
  }

  bool isLoggedIn() => _loginPlatform != LoginPlatform.none;
}
