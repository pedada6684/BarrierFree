import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/component/facility_button.dart';
import 'package:barrier_free/component/mapsearch_result_list.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/screen/map/mapresult_screen.dart';
import 'package:barrier_free/services/location_service.dart';
import 'package:barrier_free/services/place_service.dart';
import 'package:barrier_free/services/search_service.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:geolocator/geolocator.dart';
import 'package:kakaomap_webview/kakaomap_webview.dart';
import 'package:sliding_up_panel/sliding_up_panel.dart';

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  Key mapKey = UniqueKey();

  late TextEditingController _originController = TextEditingController();
  late Future<Position> _currentPositionFuture;
  late List<dynamic> allPlaces = [];
  late List<dynamic> filteredPlaces = [];
  late List<dynamic> searchResults = [];

  // late Position _currentPosition;

  String customScript = '';

  // PanelController _panelController = PanelController();

  @override
  void initState() {
    super.initState();
    _originController = TextEditingController();
    //초기화
    allPlaces = [];
    filteredPlaces = [];
    _initMapData();
    WidgetsBinding.instance.addPostFrameCallback((_) {});
  }

  //현재 위치 기반으로 마커와 이동 버튼 만들기
  void _initMapData() async {
    _currentPositionFuture = LocationService().getCurrentPosition();
    final currentPosition = await _currentPositionFuture;
    _loadPlaces(currentPosition);
    _setCustomScript(currentPosition);
    print(currentPosition);
  }

  void _setCustomScript(Position currentPosition) {
    var starMarkerScript = """
    var markerPosition = new kakao.maps.LatLng(${currentPosition.latitude}, ${currentPosition.longitude});
    var marker = new kakao.maps.Marker({
      position: markerPosition,
      map: map,
      image: new kakao.maps.MarkerImage(
        'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png',
        new kakao.maps.Size(24, 35)
      )
    });
  """;

    setState(() {
      customScript = starMarkerScript;
    });
  }

  void _loadPlaces(Position position) async {
    final places = await PlaceService().fetchPlacesByCategory(
        position.latitude.toString(), position.longitude.toString());

    setState(() {
      allPlaces = places;
      // _setInitialMarker(position, places);
    });
  }

  Future<void> _search() async {
    if (_originController.text.isNotEmpty) {
      try {
        final currentPosition = await _currentPositionFuture;
        final result = await fetchMainSearchResults(
            _originController.text, currentPosition);
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => MapResultScreen(
              searchResults: result,
              keyWord: _originController.text,
              currentPosition: currentPosition,
            ),
          ),
        );
        print(
            '=================================================================');
        print(result);
        //검색결과 화면에 표시 로직 짜기
      } catch (e) {
        print("============= 검색 실패임: $e =============");
      }
    }
  }

  void _onCategoryFiltered(String category) async {
    final currentPosition = await _currentPositionFuture;
    List<dynamic> filtered =
        allPlaces.where((place) => place['category'] == category).toList();

    if (filtered.isEmpty) {
      // 필터링된 목록이 비었을 경우, 사용자에게 알림을 주고 현재 위치에 마커를 표시합니다.
      ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("주변에 $category 관련 배리어프리 시설이 없습니다.")));

      // 현재 위치 마커와 인포윈도우 추가
      var currentLocationScript =
          _generateCurrentLocationScript(currentPosition);
      // 마커 스크립트 생성 및 적용
      var markersScript = _generateMarkersScript(filtered, currentPosition);

      var updatedScript =
          markersScript + _generateCurrentLocationScript(currentPosition);

      setState(() {
        mapKey = UniqueKey();
        customScript = updatedScript;
      });
    } else {
      // 마커 스크립트 생성 및 적용
      setState(() {
        mapKey = UniqueKey(); // 지도 새로고침
        customScript = _generateMarkersScript(filtered, currentPosition!);
      });
    }
  }

  String _generateMarkersScript(
      List<dynamic> filtered, Position currentPosition) {
    if (filtered.isEmpty) {
      return _generateCurrentLocationScript(currentPosition);
    }

    var boundsScript = """
    var bounds = new kakao.maps.LatLngBounds();
    var currentPos = new kakao.maps.LatLng(${currentPosition.latitude}, ${currentPosition.longitude});
    bounds.extend(currentPos);
    """;
    var markersScript = """
    var currentInfowindow = null; // 현재 열려 있는 인포윈도우를 추적
    var customOverlay = null; // 클릭한 마커에 연결된 커스텀 오버레이를 추적
    """;

    for (var i = 0; i < filtered.length; i++) {
      var place = filtered[i];
      markersScript += """
      var markerPosition${i} = new kakao.maps.LatLng(${place['lat']}, ${place['lng']});
      var marker${i} = new kakao.maps.Marker({
        position: markerPosition${i},
        map: map
      });
      bounds.extend(markerPosition${i});
      
      var content${i} = '<div style="background-color: white; padding: 7px 10px; border-radius: 50px; box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);" class="label"><span class="left"></span><span style="background-color: white;" class="center">${place['placeName']}</span><span class="right"></span></div>';
      
      var customOverlay${i} = new kakao.maps.CustomOverlay({
        position: markerPosition${i},
        content: content${i},  
        yAnchor: 2.5,
      });

      kakao.maps.event.addListener(marker${i}, 'click', (function(marker, customOverlay) {
       return function() {
         map.setCenter(marker.getPosition());
         map.panTo(marker.getPosition()); // 마커가 있는 위치로 부드럽게 이동
         map.setLevel(5, {animate: {duration: 700}});
          // 이전 인포윈도우 닫기
          if (currentInfowindow) {
            currentInfowindow.close();
          }
          // 이전 커스텀 오버레이 닫기
          if (customOverlay) {
            customOverlay.setMap(null);
          }
          // 클릭한 마커에 연결된 커스텀 오버레이 열기
          customOverlay.setMap(map);
          // 커스텀 오버레이 추적 업데이트
          customOverlay = customOverlay;
          // 3초 후에 커스텀 오버레이 닫기
          setTimeout(function() {
            customOverlay.setMap(null);
          }, 5000);
        };
      })(marker${i}, customOverlay${i}));
      """;
    }
    markersScript += "map.setBounds(bounds);";

    return boundsScript +
        markersScript +
        _generateCurrentLocationScript(currentPosition);
  }



  String _generateCurrentLocationScript(Position currentPosition) {
    return """
    var currentMarkerPosition = new kakao.maps.LatLng(${currentPosition.latitude}, ${currentPosition.longitude});
    var currentMarker = new kakao.maps.Marker({
      position: currentMarkerPosition,
      map: map,
      image: new kakao.maps.MarkerImage('https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png')
    });
    var currentInfowindow = new kakao.maps.InfoWindow({
      content: '<div style="padding:5px;text-align:center;">내 위치</div>'
    });
    currentInfowindow.open(map, currentMarker);
   setTimeout(function() {currentInfowindow.close();}, 3000); // `infowindow`가 아닌 `currentInfowindow`로 수정
  """;
  }

  @override
  Widget build(BuildContext context) {
    final appKey = dotenv.env['APP_KEY'];

    return FutureBuilder<Position>(
      future: _currentPositionFuture,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return Container(
            color: Colors.white,
            child: const Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                //로딩화면 맵 불러올때까지 로딩
                SpinKitPouringHourGlassRefined(
                  color: mainOrange,
                  size: 70.0,
                  duration: Duration(seconds: 1),
                ),
                SizedBox(
                  height: 8.0,
                ),
                Text(
                  'Loading',
                  style: TextStyle(
                    fontSize: 22.0,
                    fontWeight: FontWeight.bold,
                    color: mainOrange,
                  ),
                ),
              ],
            ),
          ); // 혹은 다른 로딩 표시 위젯을 반환하세요.
        } else if (snapshot.hasError) {
          return Text('위치를 가져오는 중 오류가 발생했습니다: ${snapshot.error}');
        } else {
          final position = snapshot.data!;
          if (appKey == null) {
            return const Text('환경 변수에서 앱 키를 불러올 수 없습니다.');
          }
          return Scaffold(
            resizeToAvoidBottomInset: false,
            appBar: const CustomAppBar(
              title: '베프.',
              titleStyle: TextStyle(
                fontFamily: 'LogoFont',
                fontSize: 40.0,
              ),
            ),
            body: Column(
              children: [
                Container(
                  height: 45.0,
                  width: MediaQuery.of(context).size.width * 0.8,
                  decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius:
                          const BorderRadius.all(Radius.circular(30.0)),
                      border: Border.all(color: mainOrange, width: 2),
                      boxShadow: [
                        BoxShadow(
                            color: Colors.grey.withOpacity(0.5),
                            spreadRadius: 2,
                            blurRadius: 4,
                            offset: const Offset(3, 3))
                      ]),
                  child: Row(
                    children: [
                      Expanded(
                        child: TextField(
                          textInputAction: TextInputAction.search,
                          onEditingComplete: () {
                            _search();
                            FocusScope.of(context).unfocus();
                          },
                          onSubmitted: (value) {
                            FocusScope.of(context).unfocus();
                          },
                          decoration: const InputDecoration(
                            contentPadding:
                                EdgeInsets.symmetric(horizontal: 16.0),
                            hintText: '검색어를 입력해주세요.',
                            hintStyle: TextStyle(
                                color: mainGray, fontWeight: FontWeight.w300),
                            border:
                                OutlineInputBorder(borderSide: BorderSide.none),
                          ),
                          textCapitalization: TextCapitalization.words,
                          controller: _originController,
                          onChanged: (value) {},
                        ),
                      ),
                      IconButton(
                        onPressed: _search,
                        icon: const Icon(
                          Icons.search,
                          color: mainOrange,
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(
                  height: 16.0,
                ),
                Expanded(
                  child: Stack(
                    children: [
                      Container(
                        height: MediaQuery.of(context).size.height,
                        key: mapKey,
                        child: KakaoMapView(
                          width: MediaQuery.of(context).size.width,
                          height: MediaQuery.of(context).size.height -
                              AppBar().preferredSize.height,
                          kakaoMapKey: appKey!,
                          lat: position.latitude,
                          lng: position.longitude,
                          // markerImageURL:
                          //     'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png',
                          showZoomControl: false,
                          showMapTypeControl: false,
                          customScript: customScript,
                        ),
                      ),
                      // _buildToggleButton(),
                      // SlidingUpPanel(
                      //   controller: _panelController,
                      //   panel: _buildPanel(),
                      //   collapsed: _buildCollapsedPanel(),
                      //   borderRadius: const BorderRadius.only(
                      //     topRight: Radius.circular(30.0),
                      //     topLeft: Radius.circular(30.0),
                      //   ),
                      //   minHeight: 72.0,
                      //   maxHeight: MediaQuery.of(context).size.height * 0.5,
                      // ),
                      Positioned(
                        // top: 8.0, // 위치 조정 가능
                        left: 0,
                        right: 0,
                        child: CustomFacilityButton(
                          onFeatureSelected: _onCategoryFiltered,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          );
        }
      },
    );
  }
  // Widget _buildPanel() {
  //   return MapSearchResultList(
  //     searchResults: searchResults, // 빈 리스트로 전달해도 됨
  //     startsearchResults: [], // 빈 리스트로 전달해도 됨
  //     destinationsearchResults: [],
  //   );
  // }
  //
  // Widget _buildCollapsedPanel() {
  //   return Column(
  //     children: [
  //       Container(
  //         width: MediaQuery.of(context).size.width,
  //         height: 72.0,
  //         decoration: const BoxDecoration(
  //           color: Colors.white,
  //           borderRadius: BorderRadius.only(
  //             topRight: Radius.circular(25.0),
  //             topLeft: Radius.circular(25.0),
  //           ),
  //         ),
  //         child: const Column(
  //             crossAxisAlignment: CrossAxisAlignment.center,
  //             mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //             children: [
  //               Padding(
  //                 padding: EdgeInsets.all(16.0),
  //                 child: Text(
  //                   '검색 결과',
  //                   style: TextStyle(
  //                     fontSize: 18.0,
  //                     fontWeight: FontWeight.bold,
  //                   ),
  //                 ),
  //               ),
  //             ]),
  //       ),
  //     ],
  //   );
  // }
  //
  // Widget _buildToggleButton() {
  //   return Positioned(
  //     bottom: 80.0,
  //     left: MediaQuery.of(context).size.width * 0.32,
  //     right: MediaQuery.of(context).size.width * 0.32,
  //     child: ElevatedButton(
  //       style: ElevatedButton.styleFrom(
  //           backgroundColor: Colors.white,
  //           surfaceTintColor: Colors.white,
  //           foregroundColor: mainBlack,
  //           elevation: 2
  //       ),
  //       child: const Padding(
  //         padding: EdgeInsets.all(4.0),
  //         child: Row(
  //           mainAxisAlignment: MainAxisAlignment.center,
  //           children: [
  //             Icon(
  //               Icons.list,
  //               color: mainOrange,
  //             ),
  //             SizedBox(
  //               width: 8.0,
  //             ),
  //             Text(
  //               '목록보기',
  //               style: TextStyle(fontSize: 16.0),
  //             ),
  //           ],
  //         ),
  //       ),
  //       onPressed: () {
  //         _panelController.isPanelClosed
  //             ? _panelController.open()
  //             : _panelController.close();
  //       },
  //     ),
  //   );
  // }

  @override
  void dispose() {
    _originController.dispose();
    super.dispose();
  }
}
