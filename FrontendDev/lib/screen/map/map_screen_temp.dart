import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import '../home_screen.dart';

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
      appBar: AppBar(
        centerTitle: true,
        title: Text(
          '길찾기',
          style: TextStyle(
            color: Colors.white,
          ),
        ),
        backgroundColor: Color(0xfffca63d),
      ),
      body: GoogleMap(
        mapType: MapType.normal,
        //처음 실행했을때 위치
        initialCameraPosition: initialPosition,
      ),

    );
  }
}
