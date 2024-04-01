import 'package:flutter/material.dart';
import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/component/map_markers.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/services/location_service.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:geolocator/geolocator.dart';
import 'package:kakaomap_webview/kakaomap_webview.dart';
import 'package:barrier_free/services/transitpath_service.dart';

class TransitPathMap extends StatefulWidget {
  final String? initialSearchAddress;
  final String? initialDestinationSearchAddress;

  final double? startLat;
  final double? startLon;
  final double? endLat;
  final double? endLon;

  final String? vehicleType;

  const TransitPathMap({
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
  State<TransitPathMap> createState() => _TransitPathMapState();
}

class _TransitPathMapState extends State<TransitPathMap> {
  // late List<Position> _markerPositions;
  String customScript = '';

  late Future<List<dynamic>> _busDirectionsFuture;

  @override
  void initState() {
    super.initState();
    _busDirectionsFuture = _fetchBusDirections(
      type: widget.vehicleType ?? '휠체어',
      startLat: widget.startLat ?? 0.0, // null일 경우 기본값으로 0.0 사용
      startLon: widget.startLon ?? 0.0,
      endLat: widget.endLat ?? 0.0,
      endLon: widget.endLon ?? 0.0,
    );
    _addMarkers(); // 출발지와 도착지 마커 추가
    WidgetsBinding.instance.addPostFrameCallback((_) {});
    // print('================');
    // print('StartLat: ${widget.startLat}, StartLon: ${widget.startLon}');
    // print('EndLat: ${widget.endLat}, EndLon: ${widget.endLon}');
  }

  Future<List<dynamic>> _fetchBusDirections({
    required String type,
    required double startLat,
    required double startLon,
    required double endLat,
    required double endLon,
  }) async {
    TransitPathService transitPathService = TransitPathService();
    try {
      return await transitPathService.fetchBusDirectionsResults(
        type: type,
        startLat: startLat,
        startLon: startLon,
        endLat: endLat,
        endLon: endLon,
      );
    } catch (e) {
      print('Error fetching bus directions: $e');
      return [];
    }
  }

  // Future<List<dynamic>> _fetchBusDirections() async {
  //   TransitPathService transitPathService = TransitPathService();
  //   try {
  //     return await transitPathService.fetchBusDirectionsResults(
  //         'startLat=${widget.startLat}&startLon=${widget.startLon}&endLat=${widget.endLat}&endLon=${widget.endLon}'
  //     );
  //     print(transitPathService);
  //   } catch (e) {
  //     print('Error fetching bus directions: $e');
  //     return [];
  //   }
  // }

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
      
      // 선을 구성하는 좌표 배열입니다. 이 좌표들을 이어서 선을 표시합니다
      var linePath = [
          new kakao.maps.LatLng(${widget.endLat}, ${widget.endLon}),
          new kakao.maps.LatLng(${widget.startLat}, ${widget.startLon}),
      ];

      // 지도에 표시할 선을 생성합니다
      var polyline = new kakao.maps.Polyline({
          path: linePath, // 선을 구성하는 좌표배열 입니다
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
