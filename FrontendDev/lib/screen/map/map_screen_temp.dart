import 'dart:async';

import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  static final LatLng companyLatLng = LatLng(
    36.354946759143,
    127.29980994578,
  );
  static final CameraPosition initialPosition = CameraPosition(
    target: companyLatLng,
    zoom: 18.0,
  );

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: renderAppBar(),
      body: FutureBuilder(
        future: checkPermission(),
        builder: (BuildContext context, AsyncSnapshot snapshot) {
          return Column(
            children: [
              _CustomGoogleMap(
                initialPosition: initialPosition,
              ),
            ],
          );
        },
      ),
    );
  }
}

//위치 권한 요청하기
Future<String> checkPermission() async {
  final isLocationEnabled = await Geolocator.isLocationServiceEnabled();

  if (!isLocationEnabled) {
    return '위치 서비스를 활성화 해주세요.';
  }
  //현재 어플 위치서비스에 대한 권한
  LocationPermission checkedPermission = await Geolocator.checkPermission();

  if (checkedPermission == LocationPermission.denied) {
    checkedPermission = await Geolocator.requestPermission();

    if (checkedPermission == LocationPermission.denied) {
      return '위치 권한을 허가해주세요.';
    }
  }

  if (checkedPermission == LocationPermission.deniedForever) {
    //dialog 띄울 수 없다
    return '위치 권한을 설정에서 허가해주세요.';
  }

  return '위치 권한이 허가되었습니다.';
}

AppBar renderAppBar() {
  return AppBar(
    centerTitle: true,
    title: Text(
      '길찾기',
      style: TextStyle(
        color: Colors.white,
        fontWeight: FontWeight.w700,
      ),
    ),
    backgroundColor: Color(0xfffca63d),
  );
}

class _CustomGoogleMap extends StatelessWidget {
  final CameraPosition initialPosition;

  const _CustomGoogleMap({super.key, required this.initialPosition});

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: GoogleMap(
        mapType: MapType.normal,
        //처음 실행했을때 위치
        initialCameraPosition: initialPosition,
      ),
    );
  }
}
