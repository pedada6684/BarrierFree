// 내 현재 위치 받아오기
import 'package:geolocator/geolocator.dart';

class LocationService {
  static final LocationService _instance = LocationService._internal();

  factory LocationService() {
    return _instance;
  }

  LocationService._internal();

  Position? _currentPosition;

  Position? get currentPosition => _currentPosition;

  Future<Position> getCurrentPosition() async {
    bool serviceEnabled;
    LocationPermission permission;

    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      throw '위치 서비스가 활성화 되어 있지 않습니다.';
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission != LocationPermission.whileInUse &&
          permission != LocationPermission.always) {
        throw '위치 서비스 권한이 거부되었습니다.';
      }
    }

    if (permission == LocationPermission.deniedForever) {
      throw '위치 권한을 설정에서 허가해주세요.';
    }

    return _currentPosition = await Geolocator.getCurrentPosition();
  }
}
