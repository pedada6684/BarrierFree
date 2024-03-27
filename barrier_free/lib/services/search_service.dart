import 'dart:convert';

import 'package:barrier_free/screen/map/mapresult_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
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
    // print('==============================================');
    // print(response);
    // print(data);
    return data['documents'];
  } else {
    throw Exception('검색 결과 불러오기 실패');
  }
}
