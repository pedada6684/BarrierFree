import 'dart:convert';

import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:geolocator/geolocator.dart';
import 'package:http/http.dart' as http;

Future<List<dynamic>> fetchSearchResults(String keyword) async {
  //검색어를 keyword로 받아오기
  final appKey = dotenv.env['REST_API_KEY'];
  final response = await http.get(
    Uri.parse(
        'https://dapi.kakao.com/v2/local/search/keyword.json?query=$keyword'),
    headers: {
      'Authorization': 'KakaoAK $appKey',
    },
  );

  if (response.statusCode == 200) {
    Map<String, dynamic> data = json.decode(response.body);
    return data['documents'];
  } else {
    throw Exception('검색 결과 불러오기 실패');
  }
}

Future<List<dynamic>> fetchMainSearchResults(
    String keyword, Position currentPosition) async {
  //검색어를 keyword로 받아오기
  final appKey = dotenv.env['REST_API_KEY'];
  final response = await http.get(
    Uri.parse(
        'https://dapi.kakao.com/v2/local/search/keyword.json?query=$keyword&size=10&x=${currentPosition.longitude}&y=${currentPosition.latitude}&radius=10000&sort=distance'),
    headers: {
      'Authorization': 'KakaoAK $appKey',
    },
  );

  if (response.statusCode == 200) {
    Map<String, dynamic> data = json.decode(response.body);
    List<dynamic> documents = data['documents'];
    print(documents);
    return documents;
  } else {
    throw Exception('Failed to load search results');
  }
}
