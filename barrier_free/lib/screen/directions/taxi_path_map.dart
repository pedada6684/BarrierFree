import 'package:flutter/material.dart';
import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/component/map_markers.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/services/location_service.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:geolocator/geolocator.dart';
import 'package:kakaomap_webview/kakaomap_webview.dart';

// import 'package:barrier_free/services/transitpath_service.dart';
import 'package:barrier_free/services/taxipath_service.dart';

class TaxiPathMap extends StatefulWidget {
  final String? initialSearchAddress;
  final String? initialDestinationSearchAddress;

  final double? startLat;
  final double? startLon;
  final double? endLat;
  final double? endLon;

  final String? type;

  final List<String> formattedCoordinates;
  final List<String> taxiInfo;

  final String minCost;
  final String maxCost;
  final String totalDistance;
  final String totalTime;

  const TaxiPathMap({
    super.key,
    this.initialSearchAddress,
    this.initialDestinationSearchAddress,
    this.startLat,
    this.startLon,
    this.endLat,
    this.endLon,
    this.type,
    required this.minCost,
    required this.maxCost,
    required this.totalDistance,
    required this.totalTime,
    required this.formattedCoordinates,
    required this.taxiInfo,
  });

  @override
  State<TaxiPathMap> createState() => _TaxiPathMapState();
}

class _TaxiPathMapState extends State<TaxiPathMap> {
  // late List<Position> _markerPositions;
  String customScript = '';

  // late String formattedCoordinates;

  @override
  void initState() {
    super.initState();
    _addMarkers(); // 출발지와 도착지 마커 추가
    WidgetsBinding.instance.addPostFrameCallback((_) {});
  }

  void _addMarkers() {
    double averageLat = (widget.startLat! + widget.endLat!) / 2;
    double averageLon = (widget.startLon! + widget.endLon!) / 2;

    String script = """
    map.setCenter(new kakao.maps.LatLng($averageLat, $averageLon));
    
    var sw = new kakao.maps.LatLng(${widget.endLat}, ${widget.endLon}),
    ne = new kakao.maps.LatLng(${widget.startLat}, ${widget.startLon});

    var bounds = new kakao.maps.LatLngBounds(sw, ne);
    map.setBounds(bounds);
    
    var markers = [];

    function addMarker(position) {
      var marker = new kakao.maps.Marker({position: position});
      marker.setMap(map);
      markers.push(marker);
    }
      addMarker(new kakao.maps.LatLng(${widget.endLat}, ${widget.endLon}));
      addMarker(new kakao.maps.LatLng(${widget.startLat}, ${widget.startLon}));

    var pathCoordinates = ${widget.formattedCoordinates};
    console.log(pathCoordinates);

      // 지도에 표시할 선을 생성합니다
      var polyline = new kakao.maps.Polyline({
          path: pathCoordinates, // 선을 구성하는 좌표배열 입니다
          strokeWeight: 5, // 선의 두께 입니다
          strokeColor: '#008000', // 선의 색깔입니다
          strokeOpacity: 0.7, // 선의 불투명도 입니다 1에서 0 사이의 값이며 0에 가까울수록 투명합니다
          strokeStyle: 'solid' // 선의 스타일입니다
      });

      // 지도에 선을 표시합니다
      polyline.setMap(map);
  """;
    setState(() {
      customScript = script; // 최종적으로 생성된 스크립트를 상태에 저장
    });
  }

  @override
  Widget build(BuildContext context) {
    final appKey = dotenv.env['APP_KEY'];
    final currentPosition = LocationService().currentPosition;

    int totalTimeMinutes = int.tryParse(widget.totalTime) ?? 0;
    double totalDistanceKm = (int.tryParse(widget.totalDistance) ?? 0) / 1000.0;

    print(widget.minCost);

    if (currentPosition == null) {
      return const Scaffold(
        appBar: CustomAppBar(title: '위치 정보 없음'),
        body: Center(child: Text('현재 위치 정보를 가져올 수 없습니다.')),
      );
    }

    return Scaffold(
      appBar: CustomAppBar(title: '길찾기'),
      body: Column(
        children: [
          Expanded(
            child: Stack(
              children: <Widget>[
                KakaoMapView(
                  width: MediaQuery.of(context).size.width,
                  height: MediaQuery.of(context).size.height -
                      AppBar().preferredSize.height,
                  kakaoMapKey: appKey!,
                  lat: currentPosition!.latitude,
                  lng: currentPosition!.longitude,
                  markerImageURL:
                      'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png',
                  showZoomControl: false,
                  showMapTypeControl: false,
                  draggableMarker: true,
                  customScript: customScript,
                ),
                Positioned(
                  top: 10,
                  left: 10,
                  right: 10,
                  child: Container(
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(10),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.grey.withOpacity(0.5),
                          spreadRadius: 5,
                          blurRadius: 7,
                          offset: Offset(0, 3),
                        ),
                      ],
                    ),
                    height: 140, // 적절한 높이 조정
                    child: Padding(
                      padding: EdgeInsets.fromLTRB(30.0, 12.0, 30.0, 12.0),
                      child: Center(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              '예상',
                              style: TextStyle(
                                fontSize: 23,
                                fontWeight: FontWeight.bold,
                                color: Colors.redAccent,
                              ),
                            ),
                            SizedBox(height: 10), // 위젯과 위젯 사이의 간격 조정
                            Row(
                              children: [
                                Text(
                                  '약 ${totalTimeMinutes ~/ 60}분',
                                  style: TextStyle(
                                    fontSize: 20,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                                SizedBox(width: 20), // 텍스트들 사이의 간격 조정
                                Text(
                                  '${totalDistanceKm.toStringAsFixed(2)}km',
                                  style: TextStyle(
                                    fontSize: 18,
                                    fontWeight: FontWeight.normal,
                                    color: mainGray,
                                  ),
                                ),
                              ],
                            ),
                            SizedBox(height: 8),
                            Text(
                              '${widget.minCost} ~ ${widget.maxCost} 원',
                              style: TextStyle(
                                fontSize: 20,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
