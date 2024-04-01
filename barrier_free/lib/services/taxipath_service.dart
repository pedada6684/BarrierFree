import 'dart:convert';
import 'package:http/http.dart' as http;

class TaxiPathService {
  static const String _baseUrl = 'https://hbaf.site/api/place/path/taxi';

  Future<List<dynamic>> fetchTaxiDirectionsResults({
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

    final response = await http.post(
      Uri.parse(_baseUrl),
      headers: <String, String>{
        'Content-Type': 'application/json; charset=UTF-8',
      },
      body: requestBody,
    );

    if (response.statusCode == 200) {
      final String decodedBody = utf8.decode(response.bodyBytes);
      final Map<String, dynamic> parsedJson = json.decode(decodedBody);
      print("=====================");
      print(parsedJson);
      return parsedJson['data']['geoCode'];
    } else {
      throw Exception('대중교통 경로 불러오기 실패: ${response.statusCode} ${response.reasonPhrase}');
    }
  }
}
