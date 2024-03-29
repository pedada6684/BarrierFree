import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/component/facility_button.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/screen/map/mapresult_screen.dart';
import 'package:barrier_free/services/location_service.dart';
import 'package:barrier_free/services/place_service.dart';
import 'package:barrier_free/services/search_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:geolocator/geolocator.dart';
import 'package:kakaomap_webview/kakaomap_webview.dart';

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  late TextEditingController _originController = TextEditingController();
  late Future<Position> _currentPositionFuture;
  late List<dynamic> allPlaces = [];
  late List<dynamic> filteredPlaces = [];
  late List<dynamic> searchResults = [];

  // late Position _currentPosition;

  String customScript = '';

  @override
  void initState() {
    super.initState();
    _originController = TextEditingController();
    //초기화
    allPlaces = [];
    filteredPlaces = [];
    _initMapData();
  }

  void _initMapData() async {
    _currentPositionFuture = LocationService().getCurrentPosition();
    final currentPosition = await _currentPositionFuture;
    _loadPlaces(currentPosition);
  }

  void _loadPlaces(Position position) async {
    final places = await PlaceService().fetchPlacesByCategory(
        position.latitude.toString(), position.longitude.toString());

    setState(() {
      allPlaces = places;
      _setInitialMarker(position, places);
    });
  }

  void _setInitialMarker(Position currentPosition, List<dynamic> places) {
    setState(() {
      customScript = generateCutomScript(places, currentPosition,
          showCurrentLocation: true);
    });
  }

  String generateCutomScript(List<dynamic> places, Position currentPosition,
      {bool showCurrentLocation = true}) {
    var markersScript = places.map((place) {
      return """
      var markerPosition = new kakao.maps.LatLng(${place['lat']}, ${place['lng']});
      var marker = new kakao.maps.Marker({
        position: markerPosition,
        map: map
      });
      kakao.maps.event.addListener(marker, 'click', function() {
        var infowindow = new kakao.maps.InfoWindow({
          content: '<div style="padding:5px;">${place['placeName']}</div>'
        });
        infowindow.open(map, marker);
      });
    """;
    }).join('\n');

    var currentLocationScript = showCurrentLocation
        ? """
    var currentMarkerPosition = new kakao.maps.LatLng(${currentPosition.latitude}, ${currentPosition.longitude});
    var currentMarker = new kakao.maps.Marker({
      position: currentMarkerPosition,
      map: map,
      image: new kakao.maps.MarkerImage('https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png')
    });
  """
        : '';

    return """
    ${markersScript}
    ${currentLocationScript}
    map.setBounds(new kakao.maps.LatLngBounds(new kakao.maps.LatLng(${currentPosition.latitude}, ${currentPosition.longitude})));
  """;
  }

  Future<void> _search() async {
    if (_originController.text.isNotEmpty) {
      try {
        final result = await fetchSearchResults(_originController.text);
        final currentPosition = await _currentPositionFuture;
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

  //
  void _onCategoryFiltered(String category) async {
    final currentPosition = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high);
    List<dynamic> filtered =
        allPlaces.where((place) => place['category'] == category).toList();
    setState(() {
      filteredPlaces = filtered;
    });
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
            appBar: const CustomAppBar(
              title: '베프.',
              titleStyle: TextStyle(
                fontFamily: 'LogoFont',
                fontSize: 32.0,
              ),
            ),
            body: Column(
              children: [
                Container(
                  width: MediaQuery.of(context).size.width * 0.8,
                  decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius:
                          const BorderRadius.all(Radius.circular(30.0)),
                      border: Border.all(color: mainOrange, width: 2.5),
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
                          decoration: const InputDecoration(
                            contentPadding:
                                EdgeInsets.symmetric(horizontal: 16.0),
                            hintText: '검색어를 입력해주세요.',
                            hintStyle: TextStyle(
                                color: mainGray, fontWeight: FontWeight.w600),
                            border:
                                OutlineInputBorder(borderSide: BorderSide.none),
                          ),
                          textCapitalization: TextCapitalization.words,
                          controller: _originController,
                          onChanged: (value) {
                            print(value);
                          },
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
                      KakaoMapView(
                        width: MediaQuery.of(context).size.width,
                        height: MediaQuery.of(context).size.height,
                        kakaoMapKey: appKey!,
                        lat: position.latitude,
                        lng: position.longitude,
                        markerImageURL:
                            'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png',
                        showZoomControl: false,
                        showMapTypeControl: false,
                        customScript: customScript,
                      ),
                      Positioned(
                        top: 8.0, // 위치 조정 가능
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

  @override
  void dispose() {
    _originController.dispose();
    super.dispose();
  }
}
