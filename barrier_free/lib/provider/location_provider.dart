import 'package:barrier_free/services/location_service.dart';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';


//전역에서 내 현재위치의 상태 관리
class LocationProvider with ChangeNotifier{
  Position? _currentPosition;
  
  Position? get currentPosition => _currentPosition;
  
  void updateLocation() async{
    _currentPosition = await LocationService().getCurrentPosition();
    notifyListeners();//위치 업데이트되면 리스너에게 알림
  }
}