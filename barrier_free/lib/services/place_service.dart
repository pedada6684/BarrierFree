//배리어프리 장소 받아오기

import 'dart:convert';
import 'package:http/http.dart' as http;

class PlaceService {
  static const String _baseUrl = 'https://hbaf.site/api/place';

  Future<List<dynamic>> fetchPlacesByCategory(String lat, String lng) async {
    var requestBody = jsonEncode({
      "lat": lat,
      "lng": lng,
    });

    final response = await http.post(Uri.parse('$_baseUrl/list'),
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
        },
        body: requestBody);

    if (response.statusCode == 200) {
      final String decodedBody = utf8.decode(
          response.bodyBytes); //Map<String, dynamic>형태로 온다 data 형태로 return해줘야함
      final Map<String, dynamic> parsedJson = json.decode(decodedBody);
      // print(decodedBody);
      // print(parsedJson);
      print(parsedJson['data']);
      return parsedJson['data'];

    } else {
      print('Error Status Code: ${response.statusCode}');
      print('Error Response Body: ${response.body}');
      throw Exception('장소 불러오기 실패. 상태 코드: ${response.statusCode}');
    }
  }
}

