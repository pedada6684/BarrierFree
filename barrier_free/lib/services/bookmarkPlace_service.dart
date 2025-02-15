import "dart:convert";
import "package:barrier_free/services/secure_storage_service.dart";
import "package:http/http.dart" as http;

class bookmarkPlaceService {
  final String _baseUrl = 'https://hbaf.site/api/bmplace';
  final SecureStorageService _secureStorageService = SecureStorageService();

  Future<dynamic> bookmarkPlace(int userId, String poiId, String placeName,
      String address, String latitude, String longitude) async {
    //토큰 받아오기
    String? accessToken = await _secureStorageService.getToken();
    String? cookies = await _secureStorageService.getCookies();

    final Map<String, dynamic> requestBody = {
      'memberId': userId,
      'poiId': poiId,
      'placeName': placeName,
      'address': address,
      'latitude': latitude,
      'longitude': longitude,
    };

    print(requestBody);
    print("$userId, $poiId, $placeName, $address, $latitude, $longitude");

    final response = await http
        .post(Uri.parse(_baseUrl), body: json.encode(requestBody), headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $accessToken',
      'Cookie': cookies!,
    });

    print(response.body);

    if (response.statusCode == 200) {
      final String decodedBody = utf8.decode(response.bodyBytes);
      final Map<String, dynamic> parsedJson = json.decode(decodedBody);
      return parsedJson['data'];
    } else {
      // throw Exception('북마크 장소 저장 실패');
      print(response.statusCode);
    }
  }

  Future<List<dynamic>> fetchBookMarkByUserId(int userId) async {
    final SecureStorageService _secureStorageService = SecureStorageService();
    String? accessToken = await _secureStorageService.getToken();
    String? cookies = await _secureStorageService.getCookies();
    final response =
        await http.get(Uri.parse('$_baseUrl?memberId=$userId'), headers: {
      'Authorization': 'Bearer $accessToken',
      'Cookie': cookies!,
    });

    if (response.statusCode == 200) {
      final String decodedBody = utf8.decode(response.bodyBytes);
      final data = json.decode(decodedBody);

      // print(data);

      final List<dynamic> bmList = data['list'] as List<dynamic>;

      return bmList;
    } else {
      throw Exception('내 즐겨찾기 리스트 불러오기 실패');
    }
  }
}
