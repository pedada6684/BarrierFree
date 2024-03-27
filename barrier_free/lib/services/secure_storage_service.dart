import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class SecureStorageService {
  final _storage = FlutterSecureStorage();

  Future<void> saveToken(String token) async {
    //토큰 먼저 저장
    await _storage.write(key: 'token', value: token);
    print(token);
  }

  Future<String?> getToken() async {
    //토큰 읽어와서 사용
    return await _storage.read(key: 'token');
  }

  Future<void> deleteToken() async {
    //삭제
    await _storage.delete(key: 'token');
  }
}
