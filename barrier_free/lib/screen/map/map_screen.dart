import 'package:barrier_free/component/appBar.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:geolocator/geolocator.dart';

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  static const platform = MethodChannel('com.example.barrier_free/tmap');

  Future<void> _enableTmapTrackingMode() async {
    try {
      final String result = await platform.invokeMethod('enableTrackingMode');
      print('TMap 추적모드 활성화 : $result');
    } on PlatformException catch (e) {
      print("내 위치 추적 실패:'${e.message}'");
    }
  }

  Future<Position> _determinePosition() async {
    bool serviceEnabled;
    LocationPermission permission;

    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      return Future.error('위치 서비스가 활성화 되어 있지 않습니다.');
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      //거절일경우
      permission = await Geolocator.requestPermission();
    }
    if (permission == LocationPermission.deniedForever) {
      return Future.error('위치 권한을 설정에서 허가해주세요,');
    }

    final position = Geolocator.getCurrentPosition();
    await _enableTmapTrackingMode();
    return position;
  }

  @override
  void initState() {
    super.initState();
    _determinePosition();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(
        title: '베프',
        titleStyle: TextStyle(
          fontFamily: 'LogoFont',
          fontSize: 32.0,
        ),
      ),
      body: AndroidView(
        viewType: 'showTMap',
        creationParams: {},
        creationParamsCodec: StandardMessageCodec(),
      ),
    );
  }
}
