import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/component/map_markers.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/services/location_service.dart';
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

  void _showModalBottomSheet() {
    showModalBottomSheet(
      context: context,
      isDismissible: true,
      isScrollControlled: true,
      // 전체 화면으로 확장 가능
      barrierColor: Colors.transparent,
      backgroundColor: Colors.white,
      builder: (BuildContext context) {
        double statusBarHeight = MediaQuery.of(context).padding.top;
        double appBarHeight = AppBar().preferredSize.height;
        double maxHeight = 1.0 -
            ((statusBarHeight + appBarHeight) /
                MediaQuery.of(context).size.height);

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
            maxChildSize: maxHeight,
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
            child: Stack(
              children: [
                KakaoMapView(
                  width: MediaQuery.of(context).size.width,
                  height: MediaQuery.of(context).size.height-AppBar().preferredSize.height,
                  kakaoMapKey: appKey!,
                  lat: currentPosition!.latitude,
                  lng: currentPosition!.longitude,
                  showZoomControl: false,
                  showMapTypeControl: false,
                  customScript: generateMarkerScript(widget.searchResults)+generateBoundsScript(widget.searchResults),
                ),
                _buildToggleButton(),
              ],
            ),
          ),
        ],
      ),
    );
  }

  //
  Widget _buildToggleButton() {
    double pWidth = MediaQuery.of(context).size.width * 0.32;
    return Positioned(
      bottom: 20,
      //0으로 했더니 화면 꽉채워서 양옆으로 공간주기
      left: pWidth,
      right: pWidth,
      child: Container(
        padding: EdgeInsets.symmetric(vertical: 10), // 좌우, 상하 패딩 조절
        decoration: BoxDecoration(
          color: Colors.white, // 배경색
          borderRadius: BorderRadius.circular(30), // 모서리 둥글게
          boxShadow: [
            // 그림자 효과
            BoxShadow(
              color: Colors.black26,
              blurRadius: 4,
              offset: Offset(0, 2),
            ),
          ],
        ),
        child: InkWell(
          onTap: () => _showModalBottomSheet(),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            mainAxisSize: MainAxisSize.min, // Row를 최소 크기로 설정
            children: [
              Icon(Icons.list, color: mainOrange), // 아이콘
              SizedBox(width: 10), // 아이콘과 텍스트 사이의 공간
              Text('목록보기', style: TextStyle(fontSize: 16)), // 텍스트
            ],
          ),
        ),
      ),
    );
  }

//   Widget _buildBottomSheet() {
//     return DraggableScrollableSheet(
//       expand: false,
//       builder: (BuildContext context, ScrollController scrollController) {
//         return Container(
//           padding: const EdgeInsets.all(8.0),
//           child: ListView.separated(
//             controller: scrollController,
//             itemCount: widget.searchResults.length,
//             separatorBuilder: (context, index) => Divider(height: 1),
//             itemBuilder: (BuildContext context, int index) {
//               return ListTile(
//                 title: Text(widget.searchResults[index]['place_name']),
//                 onTap: () {
//                   // 마커로 이동
//                 },
//               );
//             },
//           ),
//         );
//       },
//       initialChildSize: 0.3,
//       minChildSize: 0.3,
//       maxChildSize: 1.0,
//     );
//   }
}
