import 'package:barrier_free/component/appBar.dart';
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
  final Position currentPosition;

  const MapResultScreen({
    super.key,
    required this.searchResults,
    required this.keyWord,
    required this.currentPosition,
  });

  @override
  State<MapResultScreen> createState() => _MapResultScreenState();
}

class _MapResultScreenState extends State<MapResultScreen> {
  late List<Position> _markerPositions;
  PanelController _panelController = PanelController();

  //마커 누르면 장소 이름 나옴
  String generateMarkerScript(
      List<dynamic> searchResults, Position currentPosition) {
    String markersScript = """
    var bounds = new kakao.maps.LatLngBounds();
    var currentInfowindow = null;
  """;

    for (var i = 0; i < searchResults.length; i++) {
      var result = searchResults[i];
      markersScript += """
    
      var markerPosition${i} = new kakao.maps.LatLng(${result['y']}, ${result['x']});
      bounds.extend(markerPosition${i});
      var marker${i} = new kakao.maps.Marker({
        position: markerPosition${i},
        map: map
      });
      kakao.maps.event.addListener(marker${i}, 'click', function() {
        var infowindow = new kakao.maps.InfoWindow({
          content: '<div style="width:150px;text-align:center;padding:6px 0;">${result['place_name']}</div>'
        });
        
        if(currentInfowindow){
          currentInfowindow.close();
        }
       
      infowindow.open(map, marker${i});
      currentInfowindow = infowindow;
      
      map.panTo(markerPosition${i});
      setTimeout(function() {
        map.setLevel(3);
      }, 500);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                
      
      setTimeout(function() {
      infowindow.close();
      currentInfowindow = null; 
      }, 3000);
      });
    """;
    }
    markersScript += "map.setBounds(bounds);";
    return markersScript;
  }

  // String generateBoundsScript(Position currentPosition) {
  //   // 현재 위치에서의 반경 20km를 위한 바운드 설정
  //   double lat = widget.currentPosition.latitude;
  //   double lng = widget.currentPosition.longitude;
  //   return """
  //     var circle = new kakao.maps.Circle({
  //       center: new kakao.maps.LatLng($lat, $lng),
  //       radius: 3000, // 5km
  //       strokeWeight: 1,
  //       strokeColor: '#75B8FA',
  //       strokeOpacity: 0,
  //     });
  //     circle.setMap(map);
  //     // 바운드를 circle에 맞춤
  //     var bounds = circle.getBounds();
  //     map.setBounds(bounds);
  //   """;
  // }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _initializeMarkers();
    WidgetsBinding.instance.addPostFrameCallback((_) {});

    print(widget.searchResults);
  }

  void _initializeMarkers() {
    _markerPositions = widget.searchResults.map<Position>((result) {
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
                    markerImageURL:
                        'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png',
                    showZoomControl: false,
                    showMapTypeControl: false,
                    customScript: generateMarkerScript(
                        widget.searchResults, widget.currentPosition)),
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
                  maxHeight: MediaQuery.of(context).size.height * 0.5,
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
          width: MediaQuery.of(context).size.width,
          height: 72.0,
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
          surfaceTintColor: Colors.white,
          foregroundColor: mainBlack,
          elevation: 2
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
