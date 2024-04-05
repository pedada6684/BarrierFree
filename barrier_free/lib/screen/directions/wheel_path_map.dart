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
  final List<dynamic> recommendedGeoCodeData;
  final List<String> formattedCoordinates;

  final String totalTime;
  final String totalDistance;
  final String rcTotalTime;
  final String rcTotalDistance;

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
    required this.recommendedGeoCodeData,
    required this.rcTotalTime,
    required this.rcTotalDistance,
  });

  @override
  State<WheelPathMap> createState() => _WheelPathMapState();
}

class _WheelPathMapState extends State<WheelPathMap> {
  Key wheelKey = UniqueKey();

  // late List<Position> _markerPositions;
  String customScript = '';
  String selectedPath = 'basic';
  bool _isLoading = true;
  bool _isRecommendedPathSelected = false;
  bool _isSelected = false;
  bool _isRecommendedSelected = false;
  bool _isBasicSelected = true;

  // late String formattedCoordinates;

  @override
  void initState() {
    super.initState();
    _loadData();
    _addMarkers1(); // 출발지와 도착지 마커 추가
    WidgetsBinding.instance.addPostFrameCallback((_) {});

    print('recommendedpathCoordinates:${widget.recommendedGeoCodeData}');
    print('wheelDirections : ${widget.wheelDirections}');
  }

  Future<void> _loadData() async {
    await Future.delayed(Duration(seconds: 3));

    if (mounted) {
      setState(() {
        _isLoading = false;
        _addMarkers2();
      });
    }
  }

  //일반경로
  void _addMarkers1() {
    double averageLat = (widget.startLat! + widget.endLat!) / 2;
    double averageLon = (widget.startLon! + widget.endLon!) / 2;

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

    String pathScript = widget.recommendedGeoCodeData.map((direction) {
      double latitude = double.parse(
          direction['latitude'].toString()); // Ensure latitude is a double
      double longitude = double.parse(
          direction['longitude'].toString()); // Ensure longitude is a double
      String color = getColorForSlope(
          double.tryParse(direction['angleSlope']?.toString() ?? '0.0') ?? 0.0);

      // Directly interpolate numbers without quotes, and use quotes only for strings
      return "{lat: $latitude, lng: $longitude, color: '$color'}";
    }).join(", ");

    print('pathScript 추천 : $pathScript');

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

  //일반경로
  void _addMarkers2() {
    double averageLat = (widget.startLat! + widget.endLat!) / 2;
    double averageLon = (widget.startLon! + widget.endLon!) / 2;

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
      if (pathType == 'basic') {
        _isBasicSelected = true;
        _isRecommendedSelected = false;
        _addMarkers2();
      } else if (pathType == 'recommended' &&
          widget.recommendedGeoCodeData.isNotEmpty) {
        _isBasicSelected = false;
        _isRecommendedSelected = true;
        _addMarkers1();
      }
      wheelKey = UniqueKey();
    });
  }

  Widget _bottomButton(String title, VoidCallback onTap) {
    bool isSelected = title.contains('추천') && _isRecommendedPathSelected;
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: isSelected ? Colors.blue : Colors.white,
          borderRadius: BorderRadius.circular(10),
          boxShadow: [
            BoxShadow(
              color: Colors.grey.withOpacity(0.5),
              spreadRadius: 1,
              blurRadius: 3,
              offset: Offset(0, 3),
            ),
          ],
        ),
        child: Text(
          title,
          style: TextStyle(
            fontWeight: FontWeight.bold,
            fontSize: 16,
            color: isSelected ? Colors.white : Colors.black,
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final appKey = dotenv.env['APP_KEY'];
    final currentPosition = LocationService().currentPosition;
    int totalTimeMinutes = (int.tryParse(widget.totalTime) ?? 0) ~/ 60;
    double totalDistanceKm = double.parse(
        ((int.tryParse(widget.totalDistance) ?? 0) / 1000.0)
            .toStringAsFixed(2));
    int rcTotalTimeMinutes = (int.tryParse(widget.rcTotalTime) ?? 0) ~/ 60;
    double rcTotalDistanceKm = double.parse(
        ((int.tryParse(widget.rcTotalDistance) ?? 0) / 1000.0)
            .toStringAsFixed(2));
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
                Container(
                  key: wheelKey,
                  child: KakaoMapView(
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
                ),
                Positioned(
                  bottom: 20,
                  left: 10,
                  right: 10,
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      widget.recommendedGeoCodeData.isNotEmpty
                          ? _buildBottomButton(
                              '추천 경로',
                              () => _setPath('recommended'),
                              _isRecommendedSelected,
                              rcTotalTimeMinutes,
                              rcTotalDistanceKm)
                          : Container(
                              child: Text('추천 경로가 없습니다'),
                            ),
                      _buildBottomButton('일반 경로', () => _setPath('basic'),
                          _isBasicSelected, totalTimeMinutes, totalDistanceKm),
                    ],
                  ),
                )
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildBottomButton(String title, VoidCallback onTap, bool isSelected,
      int totalTime, double totalDistance) {
    Color textColor =  title == '추천 경로' ? mainOrange : mainBlack;
    return GestureDetector(
      onTap: onTap,
      child: AnimatedContainer(
        duration: Duration(milliseconds: 200),
        curve: Curves.easeInOut,
        padding: EdgeInsets.symmetric(vertical: 15),
        width: MediaQuery.of(context).size.width * 0.4,
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(10),
          border: Border.all(
            color: isSelected ? mainOrange : mainGray,
            width: 2,
          ),
          boxShadow: isSelected
              ? [
                  BoxShadow(
                    color: mainGray.withOpacity(0.5),
                    spreadRadius: 1,
                    blurRadius: 2,
                    offset: Offset(0, 2),
                  ),
                ]
              : [
                  BoxShadow(
                    color: mainOrange.withOpacity(0.5),
                    spreadRadius: 1,
                    blurRadius: 3,
                    offset: Offset(0, 2),
                  ),
                ],
        ),
        child: Center(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              Text(
                title,
                style: TextStyle(fontSize: 16.0, color: textColor, fontWeight: FontWeight.bold),
              ),
              Text(
                '약 $totalTime 분',
                style: TextStyle(fontSize: 20.0, fontWeight: FontWeight.bold),
              ),
              Text('$totalDistance km'),
            ],
          ),
        ),
      ),
    );
  }
}
