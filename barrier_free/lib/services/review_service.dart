import 'dart:convert';

import 'package:http/http.dart' as http;

class ReviewService {
  static const String _baseUrl = 'https://hbaf.site/api/review';

  //리뷰 불러오기
  Future<List<dynamic>> fetchReviewByPlaceId(String poiId) async {
    final response = await http.get(Uri.parse('$_baseUrl/list?poiId=$poiId'));

      print(response);
      print(response.body);
      print(response.statusCode);
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      //빈배열에도 오류 안나게
      final List<dynamic> reviewList = data['list'] as List<dynamic>;

      return reviewList;
    } else {
      throw Exception('이 장소의 리뷰 목록 불러오는데 실패');
    }
  }

  //리뷰 작성하기
}
