import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/component/facility_button.dart';
import 'package:barrier_free/const/color.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:geolocator/geolocator.dart';

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  static const platform = MethodChannel('com.barrier_free/tmap');
  late TextEditingController _originController = TextEditingController();

  Position? _currentPosition;

  @override
  void initState() {
    super.initState();
    _originController = TextEditingController();
    _getCurrentPosition().then((_) {
      _updateMapPosition(
          _currentPosition!.latitude, _currentPosition!.longitude);
    }).catchError((error) {
      print('위치 정보 가져오기 실패:$error');
    });
  }



  Future<void> _getCurrentPosition() async {
    bool serviceEnabled;
    LocationPermission permission;

    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      return Future.error('위치 서비스가 활성화 되어 있지 않습니다.');
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      //거절일경우
      permission = await Geolocator.requestPermission();
    }
    if (permission == LocationPermission.deniedForever) {
      return Future.error('위치 권한을 설정에서 허가해주세요,');
    }

    final position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high);
    platform.invokeMethod('setCurrentLocation', {
      'latitude': position.latitude,
      'longitude': position.longitude,
    });

    setState(() {
      _currentPosition = position;
    });

  }

  void _updateMapPosition(double latitude, double longitude) async{
    //네이티브로 코드 전송
    try {
      
      final String result = await platform.invokeMethod('setCurrentLocation', {
        'latitude': latitude,
        'longitude': longitude,
      });
      print('===============Result: $result===============');
    } on PlatformException catch (e) {
      print("===============Failed to set location: '${e.message}'.===============");
    }
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(
        title: '베프',
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
                      contentPadding: EdgeInsets.symmetric(horizontal: 16.0),
                      hintText: '검색어를 입력해주세요.',
                      hintStyle: TextStyle(
                          color: mainGray, fontWeight: FontWeight.w600),
                      border: OutlineInputBorder(borderSide: BorderSide.none),
                    ),
                    textCapitalization: TextCapitalization.words,
                    controller: _originController,
                    onChanged: (value) {
                      print(value);
                    },
                  ),
                ),
                IconButton(
                  onPressed: () {},
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
                AndroidView(
                  viewType: 'showTMap',
                  creationParams: {},
                  creationParamsCodec: StandardMessageCodec(),
                ),
                Positioned(
                  top: 50.0,
                  child: ElevatedButton(
                    onPressed: () {
                      if (_currentPosition != null) {
                        _updateMapPosition(_currentPosition!.latitude,
                            _currentPosition!.longitude);
                      } else {
                        print('현재 위치 정보가 없습니다.');
                      }
                    },
                    child: Text('내위치로 이동하기'),
                  ),
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

  @override
  void dispose() {
    _originController.dispose();
    super.dispose();
  }
}
