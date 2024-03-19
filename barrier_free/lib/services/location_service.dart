import 'dart:async';

import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_polyline_points/flutter_polyline_points.dart';
import 'package:http/http.dart' as http;
import 'dart:convert' as convert;

class LocationService {
  final String key = dotenv.env['appKey']!;

  Future<String> getPlaceId(String input) async {
    final String url =
        'https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=$input&inputtype=textquery&key=$key';

    var response = await http.get(Uri.parse(url));
    var json = convert.jsonDecode(response.body);
    print('플레이스 아이디');
    print(json);
    var placeId = json['candidates'][0]['place_id'] as String;

    return placeId;
  }

  Future<Map<String, dynamic>> getPlace(String input) async {
    final placeId = await getPlaceId(input);
    final String url =
        'https://maps.googleapis.com/maps/api/place/details/json?place_id=$placeId&language=ko&key=$key';
    var response = await http.get(Uri.parse(url));
    var json = convert.jsonDecode(response.body);
    var result = json['result'] as Map<String, dynamic>;

    print(result);
    print(response.body);
    return result;
  }

  Future<Map<String, dynamic>> getDirections(
      String origin, String destination) async {
    final String url =
        'https://maps.googleapis.com/maps/api/directions/json?origin=$origin&destination=$destination&language=ko&key=$key';

    var response = await http.get(Uri.parse(url));
    var json = convert.jsonDecode(response.body);
    print(response.body);
    if (json['routes'].isEmpty) {
      throw Exception("No routes found");
    }
    var route = json['routes'][0];
    if (route['legs'].isEmpty) {
      throw Exception("No legs found in the route");
    }
    var leg = route['legs'][0];
    var results = {
      'bounds_ne': route['bounds']['northeast'],
      'bounds_sw': route['bounds']['southwest'],
      'start_location': leg['start_location'],
      'end_location': leg['end_location'],
      'polyline': route['overview_polyline']['points'],
      'polyline_decoded':
          PolylinePoints().decodePolyline(route['overview_polyline']['points']),
    };

    print('---------------------------------------');
    print(response.body);
    print(results);
    return results;
  }
}

