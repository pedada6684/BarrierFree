import 'dart:async';

import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/component/facility_button.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/services/location_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_polyline_points/flutter_polyline_points.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  CameraPosition? _myCurrentSpot;
  final Completer<GoogleMapController> _controller = Completer();
  TextEditingController _originController = TextEditingController();
  TextEditingController _destinationController = TextEditingController();

  Set<Marker> _markers = Set<Marker>();
  Set<Polyline> _polylines = Set<Polyline>();
  List<LatLng> polygonLatLng = <LatLng>[];

  int _polylineIdCounter = 1;

  @override
  void initState() {
    super.initState();
    _getCurrentLocation();
  }

  Future<void> _getCurrentLocation() async {
    Position position = await _determinePosition();
    if (position.latitude != null && position.longitude != null) {
      setState(() {
        _myCurrentSpot = CameraPosition(
          target: LatLng(position.latitude!, position.longitude!),
          zoom: 18.0,
        );
        _setMarker(
          LatLng(position.latitude, position.longitude),
        );
      });
    }
  }

  //위치 권환 확인, 위치 정보 열기
  Future<Position> _determinePosition() async {
    bool serviceEnabled;
    LocationPermission permission;

    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      //위치서비스 비활성화 되어있으면 오류
      return Future.error('위치 서비스가 활성화 되어 있지 않습니다.');
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      //거절되어 있으면 창 띄우기
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        return Future.error('위치 권한이 거부되었습니다.');
      }
    }
    if (permission == LocationPermission.deniedForever) {
      return Future.error('위치 권한을 설정에서 허가해주세요.');
    }

    return await Geolocator.getCurrentPosition();
  }

  void _setMarker(LatLng point) {
    final String markerIdVal = 'marker_${_markers.length}'; // 유니크한 ID 생성
    setState(
      () {
        _markers.add(
          Marker(
            markerId: MarkerId(markerIdVal),
            position: point,
            onTap: () {
              _showMarkerInfo(point.latitude, point.longitude); // 익명 함수 내에서 호출
            },
          ),
        );
      },
    );
  }

  void _showMarkerInfo(double lat, double lng) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('위도: $lat, 경도: $lng'),
        duration: Duration(seconds: 3),
      ),
    );
  }

  void _setPolyline(List<PointLatLng> points) {
    final String polylineIdVal = 'polyline_$_polylineIdCounter';
    _polylineIdCounter++;

    List<LatLng> polylineCoordinates = points
        .map(
          (point) => LatLng(point.latitude, point.longitude),
        )
        .toList();

    _polylines.add(
      Polyline(
        polylineId: PolylineId(
          polylineIdVal,
        ),
        width: 2,
        color: Colors.blue,
        points: polylineCoordinates,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {

    return Scaffold(
      appBar: CustomAppBar(
        title: '베프.',
        titleStyle: TextStyle(
          fontFamily: 'LogoFont',
          fontSize: 32.0,
          fontWeight: FontWeight.bold,
          color: mainOrange,
        ),
      ),
      body: _myCurrentSpot == null
          ? Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                SpinKitPouringHourGlassRefined(
                  color: mainOrange,
                  size: 70.0,
                  duration: Duration(seconds: 2),
                ),
                SizedBox(
                  height: 16.0,
                ),
                Text(
                  'loading',
                  style: TextStyle(
                    color: mainOrange,
                    fontSize: 16.0,
                  ),
                ),
              ],
            )
          : Column(
              children: [
                Container(
                  width: 400.0,
                  decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.all(Radius.circular(30.0)),
                      border: Border.all(color: mainOrange, width: 2.5),
                      boxShadow: [
                        BoxShadow(
                            color: Colors.grey.withOpacity(0.5),
                            spreadRadius: 2,
                            blurRadius: 4,
                            offset: Offset(3, 3))
                      ]),
                  child: Row(
                    children: [
                      Expanded(
                        child: TextField(
                          decoration: InputDecoration(
                            contentPadding:
                                EdgeInsets.symmetric(horizontal: 16.0),
                            hintText: '검색어를 입력해주세요.',
                            hintStyle: TextStyle(
                                color: mainGray, fontWeight: FontWeight.w600),
                            border:
                                OutlineInputBorder(borderSide: BorderSide.none),
                          ),
                          textCapitalization: TextCapitalization.words,
                          controller: _originController,
                          onChanged: (value) {
                            print(value);
                          },
                        ),
                      ),
                      IconButton(
                        onPressed: () async {
                          var directions = await LocationService()
                              .getDirections(_originController.text,
                                  _destinationController.text);
                          if (directions != null) {
                            _goToStart(
                              directions['start_location']['lat'],
                              directions['start_location']['lng'],
                              directions['bounds_ne'],
                              directions['bounds_sw'],
                            );
                            _setPolyline(directions['polyline_decoded']);
                          }
                        },
                        icon: Icon(
                          Icons.search,
                          color: mainOrange,
                        ),
                      ),
                    ],
                  ),
                ),
                SizedBox(
                  height: 16.0,
                ),
                // CustomFacilityButton(onFeatureSelected: (String) {  },),
                Expanded(
                  child: Stack(
                    children: [
                      GoogleMap(
                        mapType: MapType.normal,
                        markers: _markers,
                        polylines: _polylines,
                        myLocationEnabled: true,
                        myLocationButtonEnabled: false,
                        initialCameraPosition: _myCurrentSpot!,
                        onMapCreated: (GoogleMapController controller) {
                          _controller.complete(controller);
                        },
                      ),
                      Positioned(
                        top: 8.0, // 위치 조정 가능
                        left: 0,
                        right: 0,
                        child: CustomFacilityButton(
                            onFeatureSelected: (String) {}),
                      ),
                    ],
                  ),
                ),
              ],
            ),
    );
  }

  Future<void> _goToStart(
    double lat,
    double lng,
    Map<String, dynamic> boundsNe,
    Map<String, dynamic> boundsSw,
  ) async {
    final GoogleMapController controller = await _controller.future;
    controller.animateCamera(CameraUpdate.newCameraPosition(
      CameraPosition(
        target: LatLng(lat, lng),
        zoom: 12,
      ),
    ));

    controller.animateCamera(
      CameraUpdate.newLatLngBounds(
          LatLngBounds(
              southwest: LatLng(
                boundsSw['lat'],
                boundsSw['lng'],
              ),
              northeast: LatLng(
                boundsNe['lat'],
                boundsNe['lng'],
              )),
          25),
    );

    _setMarker(
      LatLng(lat, lng),
    );
  }
}
