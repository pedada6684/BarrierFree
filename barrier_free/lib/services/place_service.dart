//배리어프리 장소 받아오기

import 'dart:convert';
import 'package:http/http.dart' as http;

class PlaceService {
  static const String _baseUrl = 'https://hbaf.site/api/place';

  Future<List<dynamic>> fetchPlacesByCategory(String category) async {
    final response = await http.get(Uri.parse('$_baseUrl/list?category=화장실'));

    if (response.statusCode == 200) {
      final String decodedBody =
          utf8.decode(response.bodyBytes); //Map<String, dynamic>형태로 온다 data 형태로 return해줘야함
      final Map<String, dynamic> parsedJson = json.decode(decodedBody);
      print(decodedBody);
      print(parsedJson);
      return parsedJson['data'];
    } else {
      throw Exception('장소 불러오기 실패');
    }
  }
}
