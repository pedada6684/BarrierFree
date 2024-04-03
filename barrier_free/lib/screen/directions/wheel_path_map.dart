import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/services/location_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:kakaomap_webview/kakaomap_webview.dart';

class WheelPathMap extends StatefulWidget {
  final String? initialSearchAddress;
  final String? initialDestinationSearchAddress;

  final double? startLat;
  final double? startLon;
  final double? endLat;
  final double? endLon;

  final String? type;
  final List<dynamic> wheelDirections;
  final List<dynamic> recommendedpathCoordinates;
  final List<String> formattedCoordinates;

  final String totalTime;
  final String totalDistance;

  const WheelPathMap({
    super.key,
    this.initialSearchAddress,
    this.initialDestinationSearchAddress,
    this.startLat,
    this.startLon,
    this.endLat,
    this.endLon,
    this.type,
    required this.formattedCoordinates,
    required this.wheelDirections,
    required this.totalTime,
    required this.totalDistance,
    required this.recommendedpathCoordinates,
  });

  @override
  State<WheelPathMap> createState() => _WheelPathMapState();
}

class _WheelPathMapState extends State<WheelPathMap> {
  // late List<Position> _markerPositions;
  String customScript = '';
  String selectedPath = 'basic';
  bool _isLoading = true;
  bool _isRecommendedPathSelected = false;

  // late String formattedCoordinates;

  @override
  void initState() {
    super.initState();
    _loadData();
    _addMarkers(); // 출발지와 도착지 마커 추가
    WidgetsBinding.instance.addPostFrameCallback((_) {});
    print(widget.type);
  }

  Future<void> _loadData() async {
    await Future.delayed(Duration(seconds: 3));

    if (mounted) {
      setState(() {
        _isLoading = false;
        _addMarkers();
      });
    }
  }

  void _addMarkers() {
    double averageLat = (widget.startLat! + widget.endLat!) / 2;
    double averageLon = (widget.startLon! + widget.endLon!) / 2;

    // List<dynamic> pathData =
    //     selectedPath == 'basic' ? widget.wheelDirections : [];

    String getColorForSlope(double slope) {
      if (slope >= 7 || slope <= -7) {
        return '#FF0000'; // 붉은색
      } else if ((slope > 5 && slope < 7) || (slope < -5 && slope > -7)) {
        return '#FF0000'; // 붉은색
      } else if ((slope >= 3 && slope <= 5) || (slope <= -3 && slope >= -5)) {
        return '#FFA500'; // 주황색
      } else if ((slope > 1.5 && slope < 3) || (slope < -1.5 && slope > -3)) {
        return '#FFFF00'; // 노란색
      } else {
        return '#90EE90'; // 연두색
      }
    }

    print('휠체어 디렉션 : ${widget.wheelDirections}');

    String pathScript = widget.wheelDirections.map((direction) {
      double latitude = double.parse(
          direction['latitude'].toString()); // Ensure latitude is a double
      double longitude = double.parse(
          direction['longitude'].toString()); // Ensure longitude is a double
      String color = getColorForSlope(
          double.tryParse(direction['angleSlope']?.toString() ?? '0.0') ?? 0.0);

      // Directly interpolate numbers without quotes, and use quotes only for strings
      return "{lat: $latitude, lng: $longitude, color: '$color'}";
    }).join(", ");

    print(pathScript);

    String script = """
    map.setCenter(new kakao.maps.LatLng($averageLat, $averageLon));
    
    var sw = new kakao.maps.LatLng(${widget.endLat}, ${widget.endLon}),
    ne = new kakao.maps.LatLng(${widget.startLat}, ${widget.startLon});
    var bounds = new kakao.maps.LatLngBounds(sw, ne);
    map.setBounds(bounds);
    
    
    var path = [$pathScript];
    for (var i = 0; i < path.length - 1; i++) {
      var polyline = new kakao.maps.Polyline({
          path: [new kakao.maps.LatLng(path[i].lat, path[i].lng), new kakao.maps.LatLng(path[i + 1].lat, path[i + 1].lng)],
          strokeWeight: 10,
          strokeColor: path[i].color,
          strokeOpacity: 1,
          strokeStyle: 'solid'
      });
      polyline.setMap(map);
    }

    
    var markers = [];

    function addMarker(position) {
      var marker = new kakao.maps.Marker({position: position});
      marker.setMap(map);
      markers.push(marker);
    }
      addMarker(new kakao.maps.LatLng(${widget.endLat}, ${widget.endLon}));
      addMarker(new kakao.maps.LatLng(${widget.startLat}, ${widget.startLon}));

    var pathCoordinates = ${widget.formattedCoordinates};
      //
      // // 지도에 표시할 선을 생성합니다
      // var polyline = new kakao.maps.Polyline({
      //     path: pathCoordinates, // 선을 구성하는 좌표배열 입니다
      //     strokeWeight: 5, // 선의 두께 입니다
      //     strokeColor: '#FFAE00', // 선의 색깔입니다
      //     strokeOpacity: 0.7, // 선의 불투명도 입니다 1에서 0 사이의 값이며 0에 가까울수록 투명합니다
      //     strokeStyle: 'solid' // 선의 스타일입니다
      // });

      // // 지도에 선을 표시합니다
      // polyline.setMap(map);
  """;
    setState(() {
      customScript = script; // 최종적으로 생성된 스크립트를 상태에 저장
    });
  }

  void _setPath(String pathType) {
    setState(() {
      _isRecommendedPathSelected = (pathType == 'recommended');
      customScript = _isRecommendedPathSelected
          ? _generatePathScript(widget.recommendedpathCoordinates)
          : _generatePathScript(widget.wheelDirections);
    });
  }

  String _generatePathScript(List<dynamic> pathData) {
    // Transform your path data into a string that can be used in the Kakao map polyline script
    String pathScript = pathData.map((direction) {
      double latitude = double.tryParse('${direction['latitude']}') ?? 0.0;
      double longitude = double.tryParse('${direction['longitude']}') ?? 0.0;
      // Add any additional properties needed for the polyline here
      return 'new kakao.maps.LatLng($latitude, $longitude)';
    }).join(',');

    // Generate the Kakao map script using the pathScript
    return """
      var linePath = [$pathScript];
      var polyline = new kakao.maps.Polyline({
          path: linePath,
          strokeWeight: 5,
          strokeColor: '#FF0000',
          strokeOpacity: 1.0,
          strokeStyle: 'solid'
      });
      polyline.setMap(map);
    """;
  }

  Widget _buildPathToggleButton(String title, String pathType) {
    return GestureDetector(
      onTap: () => _setPath(pathType),
      child: Container(
        padding: EdgeInsets.symmetric(vertical: 8.0, horizontal: 16.0),
        decoration: BoxDecoration(
          color: _isRecommendedPathSelected && pathType == 'recommended'
              ? Colors.blue
              : Colors.grey[200],
          borderRadius: BorderRadius.circular(20.0),
        ),
        child: Text(
          title,
          style: TextStyle(
            fontSize: 14.0,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final appKey = dotenv.env['APP_KEY'];
    final currentPosition = LocationService().currentPosition;
    int totalTimeMinutes = int.tryParse(widget.totalTime) ?? 0;
    double totalDistanceKm = (int.tryParse(widget.totalDistance) ?? 0) / 1000.0;

    if (currentPosition == null) {
      return const Scaffold(
        appBar: CustomAppBar(title: '위치 정보 없음'),
        body: Center(child: Text('현재 위치 정보를 가져올 수 없습니다.')),
      );
    }

    if (_isLoading) {
      return Scaffold(
        body: Center(
          child: CircularProgressIndicator(),
        ),
      );
    }

    return Scaffold(
      appBar: CustomAppBar(title: '도보'),
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
                      border: Border.all(color: mainOrange, width: 2),
                      borderRadius: BorderRadius.circular(10),
                      boxShadow: [
                        BoxShadow(
                          color: mainGray.withOpacity(0.5),
                          spreadRadius: 5,
                          blurRadius: 7,
                          offset: Offset(0, 3),
                        ),
                      ],
                    ),
                    height: 150, // 적절한 높이 조정
                    child: Padding(
                      padding: EdgeInsets.fromLTRB(30.0, 12.0, 30.0, 12.0),
                      child: Center(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text(
                                  '예상',
                                  style: TextStyle(
                                    fontSize: 23,
                                    fontWeight: FontWeight.bold,
                                    color: Colors.redAccent,
                                  ),
                                ),
                                Text(
                                  widget.type!,
                                  style: TextStyle(
                                      fontSize: 16.0, color: mainGray),
                                ),
                              ],
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
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                _buildPathToggleButton('일반 경로', 'basic'),
                                _buildPathToggleButton('추천 경로', 'recommended'),
                              ],
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
