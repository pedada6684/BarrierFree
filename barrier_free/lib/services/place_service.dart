//배리어프리 장소 받아오기

import 'dart:convert';
import 'package:http/http.dart' as http;

class PlaceService {
  static const String _baseUrl = 'http://localhost:8080/place';

  Future<List<dynamic>> fetchPlacesByCategory(String category) async {
    final response = await http.get(Uri.parse('$_baseUrl/list?category=화장실'));

    if (response.statusCode == 200) {
      print(response.body);
      return json.decode(response.body);
    } else {
      throw Exception('장소 불러오기 실패');
    }
  }
}
