import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/component/facility_button.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/screen/search/mapresult_screen.dart';
import 'package:barrier_free/services/location_service.dart';
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
  late List<dynamic> places = [];
  late List<dynamic> searchResults = [];
  late Position _currentPosition;

  @override
  void initState() {
    super.initState();
    _originController = TextEditingController();
    _initializeLocation();
  }

  void _initializeLocation() async {
    await LocationService().getCurrentPosition();
    setState(() {
      _currentPosition = LocationService().currentPosition!;
    });
  }

  Future<void> _search() async {
    if (_originController.text.isNotEmpty) {
      try {
        final result = await fetchSearchResults(_originController.text);
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => MapResultScreen(searchResults: result, keyWord: _originController.text),
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
  // _loadPlaces() async {
  //   try {
  //     places = await PlaceService().fetchPlacesByCategory('화장실');
  //     setState(() {});
  //   } catch (e) {
  //     print('=================장소 불러오다가 에러 발생함 $e=================}');
  //   }
  // }

  @override
  Widget build(BuildContext context) {
    final appKey = dotenv.env['APP_KEY'];

    return FutureBuilder<Position>(
      future:
          Geolocator.getCurrentPosition(desiredAccuracy: LocationAccuracy.high),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              //로딩화면 맵 불러올때까지 로딩
              SpinKitPouringHourGlassRefined(
                color: mainOrange,
                size: 70.0,
                duration: Duration(seconds: 2),
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
          ); // 혹은 다른 로딩 표시 위젯을 반환하세요.
        } else if (snapshot.hasError) {
          return Text('위치를 가져오는 중 오류가 발생했습니다: ${snapshot.error}');
        } else {
          final position = snapshot.data!;
          if (appKey == null) {
            return Text('환경 변수에서 앱 키를 불러올 수 없습니다.');
          }
          return Scaffold(
            appBar: CustomAppBar(
              title: '베프.',
              titleStyle: TextStyle(
                fontFamily: 'LogoFont',
                fontSize: 32.0,
              ),
            ),
            body: Column(
              children: [
                Container(
                  width: 400.0,
                  decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.all(Radius.circular(30.0)),
                      border: Border.all(color: mainOrange, width: 2.5),
                      boxShadow: [
                        BoxShadow(
                            color: Colors.grey.withOpacity(0.5),
                            spreadRadius: 2,
                            blurRadius: 4,
                            offset: Offset(3, 3))
                      ]),
                  child: Row(
                    children: [
                      Expanded(
                        child: TextField(
                          decoration: InputDecoration(
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
                        icon: Icon(
                          Icons.search,
                          color: mainOrange,
                        ),
                      ),
                    ],
                  ),
                ),
                SizedBox(
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
                        showZoomControl: true,
                      ),
                      Positioned(
                        top: 8.0, // 위치 조정 가능
                        left: 0,
                        right: 0,
                        child: CustomFacilityButton(
                          onFeatureSelected: (String) {},
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
