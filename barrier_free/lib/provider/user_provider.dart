import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:barrier_free/main.dart';
import 'package:barrier_free/screen/map/map_screen.dart';
import 'package:path/path.dart';
import 'package:http/http.dart' as http;

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
  Future<void> signOut(BuildContext context) async {
    final SecureStorageService _secureStorageService = SecureStorageService();

    String? accessToken = await _secureStorageService.getToken();
    String? cookies = await _secureStorageService.getCookies();

    final response = await http.get(
        Uri.parse('https://hbaf.site/api/v1/auth/logout?memberId=$userId'),
        headers: {
          'Authorization': 'Bearer $accessToken',
          'Cookie': cookies!,
        });

    if (response.statusCode == 200) {
      await FlutterNaverLogin.logOutAndDeleteToken();
      _loginPlatform = LoginPlatform.none; //플랫폼 초기화\
      final secureStorageService = SecureStorageService();
      await secureStorageService.deleteToken();

      globalPersistentTabController.jumpToTab(0);

      notifyListeners();
    } else {
      print('로그아웃 실패');
    }

    //네이버일경우

    //토큰 삭제!!
  }

  void setUserId(int userId) {
    _userId = userId;
    print('userId 세팅 완료');
    notifyListeners();
  }

  Future<void> updateProfileImage(File image) async {
    var _baseUri = 'https://hbaf.site/api/member';
    try {
      var uri = Uri.parse('$_baseUri/profile');
      var request = http.MultipartRequest('POST', uri)
        ..fields['memberId'] = _userId.toString()
        ..files.add(await http.MultipartFile.fromPath('profileImg', image.path,
            filename: basename(image.path)));

      var streamedResponse = await request.send();

      if (streamedResponse.statusCode == 200) {
        var response = await http.Response.fromStream(streamedResponse);
        var responseData = json.decode(response.body);
        print(responseData);
        String newProfileImgUrl = responseData['profileImgUrl'];
        if (newProfileImgUrl != null) {
          _profileImage = newProfileImgUrl;
          notifyListeners();
          // print('프로필 이미지 업데이트 얄루');
        }
      } else {
        print('프로필 이미지 업로드 실패');
      }
    } catch (e) {
      print(e.toString());
    }
  }

  bool isLoggedIn() => _loginPlatform != LoginPlatform.none;
}
