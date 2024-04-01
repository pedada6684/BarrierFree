import 'package:flutter/material.dart';

class TextProvider extends ChangeNotifier {
  String _originText = '';
  String _destinationText = '';

  double _startLat = 0.0;
  double _startLon = 0.0;
  double _endLat = 0.0;
  double _endLon = 0.0;

  String get originText => _originText;
  String get destinationText => _destinationText;

  void setOriginText(String text) {
    _originText = text;
    notifyListeners();
  }

  void setDestinationText(String text) {
    _destinationText = text;
    notifyListeners();
  }

  // 출발지 위도 가져오기
  double get startLat => _startLat;

  // 출발지 위도 설정하기
  void setStartLat(double lat) {
    _startLat = lat;
    notifyListeners();
  }

  // 출발지 경도 가져오기
  double get startLon => _startLon;

  // 출발지 경도 설정하기
  void setStartLon(double lon) {
    _startLon = lon;
    notifyListeners();
  }

  // 도착지 위도 가져오기
  double get endLat => _endLat;

  // 도착지 위도 설정하기
  void setEndLat(double lat) {
    _endLat = lat;
    notifyListeners();
  }

  // 도착지 경도 가져오기
  double get endLon => _endLon;

  // 도착지 경도 설정하기
  void setEndLon(double lon) {
    _endLon = lon;
    notifyListeners();
  }
}
