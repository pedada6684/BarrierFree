import 'dart:convert';
import 'package:http/http.dart' as http;

class WheelPathService {
  static const String _baseUrl = 'https://hbaf.site/api/place/path/wheel';

  Future<Map<String, dynamic>> fetchWheelDirectionsResults({
    required String type,
    required double startLat,
    required double startLon,
    required double endLat,
    required double endLon,
  }) async {
    // 요청 본문 생성
    final requestBody = jsonEncode({
      'type': type,
      'startLat': startLat.toString(),
      'startLng': startLon.toString(),
      'endLat': endLat.toString(),
      'endLng': endLon.toString(),
    });

    // print('리퀘스트바디 : $requestBody');

    final response = await http.post(
      Uri.parse(_baseUrl),
      headers: <String, String>{
        'Content-Type': 'application/json; charset=UTF-8',
      },
      body: requestBody,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body)['data'];
      if (data.isNotEmpty) {
        print('data[0]: ${data[0]}');
        print('data[1]: ${data[1]}');

        return {
          'basicPath': data[0], // 일반 경로 항상 존재
          'recommendedPath': data.length > 1 ? data[1] : null, // 추천 경로는 선택적
        };
      } else {
        throw Exception('No data available.');
      }
    } else {
      throw Exception(
          'Failed to load wheel directions: ${response.statusCode}');
    }
  }
}
