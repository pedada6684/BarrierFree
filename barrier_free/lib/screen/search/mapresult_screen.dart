import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/services/location_service.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:geolocator/geolocator.dart';
import 'package:kakaomap_webview/kakaomap_webview.dart';

class MapResultScreen extends StatefulWidget {
  final List<dynamic> searchResults;
  final String keyWord;

  const MapResultScreen(
      {super.key, required this.searchResults, required this.keyWord});

  @override
  State<MapResultScreen> createState() => _MapResultScreenState();
}

class _MapResultScreenState extends State<MapResultScreen> {
  late List<Position> _markerPositions;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _initializeMarkers();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _showModalBottomSheet();
    });
  }

  void _initializeMarkers() {
    _markerPositions = widget.searchResults.map<Position>((result) {
      // API에서 반환된 위치 데이터
      return Position(
        latitude: double.parse(result['y']),
        longitude: double.parse(result['x']),
        timestamp: DateTime.now(),
        accuracy: 0,
        altitude: 0,
        heading: 0,
        speed: 0,
        speedAccuracy: 0,
        altitudeAccuracy: 0,
        headingAccuracy: 0,
      );
    }).toList();
  }

  void _showModalBottomSheet() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true, // 전체 화면으로 확장 가능
      builder: (BuildContext context) {
        return Padding(
          padding: const EdgeInsets.all(8.0),
          child: DraggableScrollableSheet(
            expand: false,
            builder: (BuildContext context, ScrollController scrollController) {
              return Container(
                child: ListView.separated(
                  controller: scrollController,
                  itemCount: widget.searchResults.length,
                  separatorBuilder: (context, index) => Divider(height: 1),
                  itemBuilder: (BuildContext context, int index) {
                    return ListTile(
                      title: Text(widget.searchResults[index]['place_name']),
                      onTap: () {
                        // TODO: 그 장소로 화면 이동시키고 상세화면 또 다시 아래에 띄우기
                      },
                    );
                  },
                ),
              );
            },
            initialChildSize: 0.3,
            minChildSize: 0.3,
            maxChildSize: 0.8,
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final appKey = dotenv.env['APP_KEY'];
    final currentPosition = LocationService().currentPosition;

    if (currentPosition == null) {
      return Scaffold(
        appBar: CustomAppBar(title: '위치 정보 없음'),
        body: Center(child: Text('현재 위치 정보를 가져올 수 없습니다.')),
      );
    }



    return Scaffold(
      appBar: CustomAppBar(title: widget.keyWord),
      body: Column(
        children: [
          Expanded(
            child: KakaoMapView(
              width: MediaQuery.of(context).size.width,
              height: MediaQuery.of(context).size.height,
              kakaoMapKey: appKey!,
              lat: currentPosition!.latitude,
              lng: currentPosition!.longitude,
              showZoomControl: true,
            ),
          ),
        ],
      ),
    );
  }
}
