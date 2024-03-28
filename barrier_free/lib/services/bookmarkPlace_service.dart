import "dart:convert";
import "package:http/http.dart" as http;

class bookmarkPlaceService{
  final String _baseUrl = 'https://hbaf.site/api/bmplace';

  Future<dynamic> bookmarkPlace(int memberId, String poiId, String placeName, String address, String latitude, String longitude) async {

    final Map<String, dynamic> requestBody = {
      'memberId': memberId,
      'poiId': poiId,
      'placeName': placeName,
      'address': address,
      'latitude': latitude,
      'longitude': longitude,
    };

    final response = await http.post(
      Uri.parse(_baseUrl),
      body: json.encode(requestBody),
      headers: {'Content-Type' : 'application/json'}
    );

    if (response.statusCode == 200) {
      final String decodedBody = utf8.decode(response.bodyBytes);
      final Map<String, dynamic> parsedJson = json.decode(decodedBody);
      return parsedJson['data'];
    } else {
      throw Exception('북마크 장소 저장 실패');
    }
  }
}