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

  final String? vehicleType;

  const TaxiPathMap({
    super.key,
    this.initialSearchAddress,
    this.initialDestinationSearchAddress,
    this.startLat,
    this.startLon,
    this.endLat,
    this.endLon,
    this.vehicleType,
  });

  @override
  State<TaxiPathMap> createState() => _TaxiPathMapState();
}

class _TaxiPathMapState extends State<TaxiPathMap> {
  // late List<Position> _markerPositions;
  String customScript = '';


  @override
  void initState() {
    super.initState();
    _fetchTaxiDirections(); // 택시 경로 데이터 가져오기
    _addMarkers(); // 출발지와 도착지 마커 추가
    WidgetsBinding.instance.addPostFrameCallback((_) {});
  }

  void _fetchTaxiDirections() async {
    try {
      final taxiDirections = await TaxiPathService().fetchTaxiDirectionsResults(
        type: widget.vehicleType!,
        startLat: widget.startLat!,
        startLon: widget.startLon!,
        endLat: widget.endLat!,
        endLon: widget.endLon!,
      );
      print('taxiDirections = ${taxiDirections}');
    } catch (e) {
      print('Error fetching taxi directions: $e');
      // 에러 처리
    }
  }

  void _addMarkers() {
    // 출발지 마커 생성
    String script = """
    var markers = [];

    function addMarker(position) {
      var marker = new kakao.maps.Marker({position: position});
      marker.setMap(map);
      markers.push(marker);
    }
      addMarker(new kakao.maps.LatLng(${widget.endLat}, ${widget.endLon}));
      addMarker(new kakao.maps.LatLng(${widget.startLat}, ${widget.startLon}));

     var pathCoordinates = [];

      // 각 좌표 쌍을 LatLng 객체로 변환하여 pathCoordinates 배열에 추가
      for (var i = 0; i < taxiDirections.length; i++) {
          var coordinate = taxiDirections[i];
          var latitude = Number(coordinate[1]); // 위도를 부동소수점으로 변환
          var longitude =Number(coordinate[0]); // 경도를 부동소수점으로 변환
          var latLng = new kakao.maps.LatLng(latitude, longitude);
          pathCoordinates.push(latLng);
      }

      // 지도에 표시할 선을 생성합니다
      var polyline = new kakao.maps.Polyline({
          path: pathCoordinates, // 선을 구성하는 좌표배열 입니다
          strokeWeight: 5, // 선의 두께 입니다
          strokeColor: '#FFAE00', // 선의 색깔입니다
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
                  markerImageURL: 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png',
                  showZoomControl: false,
                  showMapTypeControl: false,
                  draggableMarker: true,
                  customScript: customScript,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}