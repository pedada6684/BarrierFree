import 'dart:async';

import 'package:app_settings/app_settings.dart';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  GoogleMapController? mapController;
  static final LatLng companyLatLng = LatLng(
    36.354946759143,
    127.29980994578,
  );
  static final CameraPosition initialPosition = CameraPosition(
    target: companyLatLng,
    zoom: 18.0,
  );
  static final Marker marker = Marker(
    markerId: MarkerId('marker'),
    position: companyLatLng,
  );

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
      actions: [
        IconButton(
          onPressed: () async {
            if (mapController == null) {
              return;
            }
            final location = await Geolocator.getCurrentPosition();
            mapController!.animateCamera(
              CameraUpdate.newLatLng(
                LatLng(location.latitude, location.longitude),
              ),
            );
          },
          icon: Icon(
            Icons.my_location,
            color: Colors.white,
          ),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: renderAppBar(),
      body: FutureBuilder(
        future: checkPermission(),
        builder: (BuildContext context, AsyncSnapshot snapshot) {
          //로딩
          if (snapshot.connectionState == ConnectionState.waiting) {
            return Center(
              child: CircularProgressIndicator(),
            );
          }

          if (snapshot.data == '위치 권한이 허가되었습니다.') {
            return Column(
              children: [
                _CustomGoogleMap(
                  initialPosition: initialPosition,
                  marker: marker,
                  onMapCreated: onMapCreated,
                ),
              ],
            );
          } else if (snapshot.data == '위치 권한을 허가해주세요.') {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  Text(snapshot.data!), // 권한 상태 메시지 표시
                  SizedBox(height: 20),
                  ElevatedButton(
                    onPressed: () async {
                      final permission = await Geolocator.requestPermission();
                      if (permission == LocationPermission.whileInUse ||
                          permission == LocationPermission.always) {
                        setState(() {}); // 권한 상태 업데이트 후 UI 재구성
                      } else if (permission ==
                          LocationPermission.deniedForever) {
                        AppSettings.openAppSettings(); // 앱 설정으로 이동
                      }
                    },
                    child: Text('권한 요청'),
                    style: ElevatedButton.styleFrom(
                      foregroundColor: Colors.white,
                      backgroundColor: Color(0xfffca63d),
                    ),
                  ),
                ],
              ),
            );
          }

          return Center(
            child: Text(snapshot.data),
          );
        },
      ),
    );
  }

  onMapCreated(GoogleMapController controller) {
    mapController = controller;
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
    //dialog 띄울 수 없다 모바일 설정 화면으로 보내기
    // Future.microtask(() => AppSettings.openAppSettings());
    return '위치 권한을 설정에서 허가해주세요.';
  }
  return '위치 권한이 허가되었습니다.';
}

class _CustomGoogleMap extends StatelessWidget {
  final CameraPosition initialPosition;
  final Marker marker;
  final MapCreatedCallback onMapCreated;

  const _CustomGoogleMap(
      {super.key,
      required this.initialPosition,
      required this.marker,
      required this.onMapCreated});

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: GoogleMap(
        mapType: MapType.normal,
        //처음 실행했을때 위치
        initialCameraPosition: initialPosition,
        myLocationEnabled: true,
        myLocationButtonEnabled: false,
        //내위치로 가는 버튼
        markers: Set.from([marker]),
        onMapCreated: onMapCreated,
      ),
    );
  }
}
