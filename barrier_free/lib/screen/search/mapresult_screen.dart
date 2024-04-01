import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/component/map_markers.dart';
import 'package:barrier_free/component/mapsearch_result_list.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/services/location_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:geolocator/geolocator.dart';
import 'package:kakaomap_webview/kakaomap_webview.dart';
import 'package:sliding_up_panel/sliding_up_panel.dart';

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
  PanelController _panelController = PanelController();

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _initializeMarkers();
    WidgetsBinding.instance.addPostFrameCallback((_) {});
  }

  void _initializeMarkers() {
    _markerPositions = widget.searchResults.map<Position>((result) {
      // print(result['y']);
      // print(result['x']);
      // API에서 반환된 위치 데이터
      return Position(
        latitude: double.parse(result['y'].toString()),
        longitude: double.parse(result['x'].toString()),
        timestamp: DateTime.now(),
        accuracy: 0,
        altitude: 0,
        heading: 0,
        speed: 0,
        speedAccuracy: 0,
        altitudeAccuracy: 0,
        headingAccuracy: 0,
        floor: 0,
      );
    }).toList();
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
      appBar: CustomAppBar(title: widget.keyWord),
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
                  customScript: generateMarkerScript(widget.searchResults) +
                      generateBoundsScript(widget.searchResults),
                ),
                _buildToggleButton(),
                SlidingUpPanel(
                  controller: _panelController,
                  panel: _buildPanel(),
                  collapsed: _buildCollapsedPanel(),
                  borderRadius: const BorderRadius.only(
                    topRight: Radius.circular(30.0),
                    topLeft: Radius.circular(30.0),
                  ),
                  minHeight: 72.0,
                  maxHeight: MediaQuery.of(context).size.height * 0.7,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildPanel() {
    return MapSearchResultList(
      searchResults: widget.searchResults, // 빈 리스트로 전달해도 됨
      startsearchResults: [], // 빈 리스트로 전달해도 됨
      destinationsearchResults: [],
    );
  }

  Widget _buildCollapsedPanel() {
    return Column(
      children: [
        Container(
          decoration: const BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.only(
              topRight: Radius.circular(25.0),
              topLeft: Radius.circular(25.0),
            ),
          ),
          child: const Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Padding(
                  padding: EdgeInsets.all(16.0),
                  child: Text(
                    '검색 결과',
                    style: TextStyle(
                      fontSize: 18.0,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ]),
        ),
      ],
    );
  }

  Widget _buildToggleButton() {
    return Positioned(
      bottom: 80.0,
      left: MediaQuery.of(context).size.width * 0.32,
      right: MediaQuery.of(context).size.width * 0.32,
      child: ElevatedButton(
        style: ElevatedButton.styleFrom(
          backgroundColor: Colors.white,
          foregroundColor: mainBlack,
        ),
        child: const Padding(
          padding: EdgeInsets.all(4.0),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                Icons.list,
                color: mainOrange,
              ),
              SizedBox(
                width: 8.0,
              ),
              Text(
                '목록보기',
                style: TextStyle(fontSize: 16.0),
              ),
            ],
          ),
        ),
        onPressed: () {
          _panelController.isPanelClosed
              ? _panelController.open()
              : _panelController.close();
        },
      ),
    );
  }
}
