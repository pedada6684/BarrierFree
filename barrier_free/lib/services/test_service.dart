import 'dart:convert';
import 'package:http/http.dart' as http;

import 'secure_storage_service.dart';

class TestService {
  static const String baseUrl = 'http://localhost:8080/api'; // 백엔드의 기본 URL로 대체되어야 합니다.

  Future<void> sendTestRequest() async {
    final url = Uri.parse('$baseUrl/v1/auth/test');
    final SecureStorageService _secureStorageService = SecureStorageService();
    String? accessToken = await _secureStorageService.getToken();
    String? cookies = await _secureStorageService.getCookies();

    try {
      final response = await http.get(
        url,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $accessToken',
          'Cookie': cookies!,

        },
      );
      print('테스트 요청 :${response.statusCode}');

      if (response.statusCode == 200) {
        print('테스트 요청이 성공했습니다.');
      } else {
        print('테스트 요청이 실패했습니다. 상태 코드: ${response.statusCode}');
      }
    } catch (error) {
      print('테스트 요청 중 오류가 발생했습니다: $error');
    }
  }
}