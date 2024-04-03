import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/providers/text_provider.dart';
import 'package:barrier_free/screen/directions/directionssearch_result_list.dart';
import 'package:barrier_free/screen/directions/taxi_path_map.dart';
import 'package:barrier_free/screen/directions/transit_path_map.dart';
import 'package:barrier_free/screen/directions/wheel_path_map.dart';
import 'package:barrier_free/services/search_service.dart';
import 'package:barrier_free/services/taxipath_service.dart';
import 'package:barrier_free/services/transitpath_service.dart';
import 'package:barrier_free/services/wheelpath_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:geolocator/geolocator.dart';
import 'package:provider/provider.dart';
import 'package:sliding_up_panel/sliding_up_panel.dart';

class DirectionsScreen extends StatefulWidget {
  final String? initialSearchAddress;
  final String? initialDestinationSearchAddress;

  final String? type;
  final double? startLat;
  final double? startLon;
  final double? endLat;
  final double? endLon;

  const DirectionsScreen({
    super.key,
    this.initialSearchAddress,
    this.initialDestinationSearchAddress,
    this.startLat,
    this.startLon,
    this.endLat,
    this.endLon,
    this.type,
  });

  @override
  State<DirectionsScreen> createState() => _DirectionsScreenState();
}

class _DirectionsScreenState extends State<DirectionsScreen> {
  late List<dynamic> wheelDirections = [];

  late List<dynamic> startsearchResults = [];
  late List<dynamic> destinationsearchResults = [];

  late String startkeyWord = '';
  late String destinationkeyWord = '';

  late TextEditingController _originController;
  late TextEditingController _destinationController;

  // PanelController _panelController = PanelController();
  bool _showSearchResults = false;

  final _vehicles = ['목발', '휠체어', '전동휠체어'];
  String? _selectedVehicles;
  Position? _currentPosition;

  late List<dynamic> recommendedPathCoordinates = [];
  late String transitCoordinates = '';
  late String wheelCoordinates = '';
  late String taxiCoordinates = '';
  late String taxiInfo = '';
  late String minCost = '';
  late String maxCost = '';
  late String totalDistance = '';
  late String totalTime = '';

  late List<dynamic> busDirections = [];

  void _fetchTaxiDirections() async {
    print(_selectedVehicles);
    try {
      final taxiDirections = await TaxiPathService().fetchTaxiDirectionsResults(
        type: _selectedVehicles!,
        startLat: widget.startLat!,
        startLon: widget.startLon!,
        endLat: widget.endLat!,
        endLon: widget.endLon!,
      );

      // minCost 값을 가져와서 minCost 변수에 할당
      String minCostinfo = taxiDirections['minCost'];
      String maxCostinfo = taxiDirections['maxCost'];
      String totalDistanceinfo = taxiDirections['totalDistance'];
      String totalTimeinfo = taxiDirections['totalTime'];

      List<dynamic> geoCodeData = taxiDirections['geoCode'];
      List<String> taxiDataString = [];

      // taxiDirections.forEach((key, value) {
      //   taxiDataString.add('$key: $value');
      // });

      // 택시 경로에서 좌표 데이터를 가져와서 포맷합니다.
      List<String> coordinatesList = geoCodeData
          .map((direction) =>
              'new kakao.maps.LatLng(${direction['latitude']}, ${direction['longitude']})')
          .toList();
      String newFormattedCoordinates = '[${coordinatesList.join(', ')}]';

      setState(() {
        taxiCoordinates = newFormattedCoordinates; // 포맷된 좌표 데이터를 상태에 저장합니다.
        taxiInfo = taxiDataString.toString();
        minCost = minCostinfo;
        maxCost = maxCostinfo;
        totalDistance = totalDistanceinfo;
        totalTime = totalTimeinfo;
        // print('===============택시===================');
        // print('taxiDirections = ${taxiDirections}');
        // // print('formattedCoordinates = ${taxiCoordinates}');
        // print('taxiDataString = ${taxiDataString}');
        // print(minCost);
      });
    } catch (e) {
      print('Error fetching taxi directions: $e');
      // 에러 처리
    }
  }

  void _fetchWheelDirections() async {

    try {
      final directionsResult =
          await WheelPathService().fetchWheelDirectionsResults(
        type: _selectedVehicles!,
        startLat: widget.startLat!,
        startLon: widget.startLon!,
        endLat: widget.endLat!,
        endLon: widget.endLon!,
      );

      print('API response: $directionsResult');

      // Map<String, dynamic>? basicPath 변경
      final basicPath = directionsResult['basicPath'] as Map<String, dynamic>?;
      final recommendedPath =
          directionsResult['recommendedPath'] as Map<String, dynamic>?;

      // null 체크 후 처리
      if (basicPath != null) {
        List<dynamic> geoCodeData = basicPath['geoCode'];
        List<String> coordinatesList = geoCodeData
            .map<String>((direction) =>
                'new kakao.maps.LatLng(${direction['latitude']}, ${direction['longitude']})')
            .toList();
        wheelCoordinates = '[${coordinatesList.join(', ')}]';
      }

      // 추천 경로 데이터가 있는 경우 처리
      if (recommendedPath != null && recommendedPath.containsKey('geoCode')) {
        List<dynamic> recommendedGeoCodeData = recommendedPath['geoCode'];
        List<String> recommendedCoordinatesList = recommendedGeoCodeData
            .map<String>((direction) =>
                'new kakao.maps.LatLng(${direction['latitude']}, ${direction['longitude']})')
            .toList();
        recommendedPathCoordinates = recommendedCoordinatesList;
      }

      setState(() {
        // 상태 업데이트
        totalDistance = basicPath?['totalDistance'].toString() ?? '';
        totalTime = basicPath?['totalTime'].toString() ?? '';
        wheelDirections = basicPath?['geoCode'] ?? [];
      });
    } catch (e) {
      print('Error fetching wheel directions: $e');
    }
  }

  void _fetchBusDirections() async {
    try {
      final busDirections =
          await TransitPathService().fetchBusDirectionsResults(
        type: _selectedVehicles!,
        startLat: widget.startLat!,
        startLon: widget.startLon!,
        endLat: widget.endLat!,
        endLon: widget.endLon!,
      );

      List<dynamic> coordinatesList =
          busDirections[0]["subPaths"][1]["passStationGeo"];
      List<String> formattedCoordinatesList = coordinatesList
          .map((direction) =>
              'new kakao.maps.LatLng(${direction['latitude']}, ${direction['longitude']})')
          .toList();
      String newFormattedCoordinates =
          '[${formattedCoordinatesList.join(', ')}]';

      setState(() {
        transitCoordinates = newFormattedCoordinates; // 포맷된 좌표 데이터를 상태에 저장합니다.
        this.busDirections = busDirections;
        print('===============버스 ===================');
        print('formattedCoordinates = ${transitCoordinates}');
        print('busDirections = ${busDirections}');
      });
    } catch (e) {
      print('=========================================');
      print('Error fetching 버스 directions: $e');
      // 에러 처리
    }
  }

  @override
  void initState() {
    super.initState();
    //
    // _fetchWheelDirections();
    // _fetchBusDirections();
    // _fetchTaxiDirections();
    _originController = TextEditingController(
        text: Provider.of<TextProvider>(context, listen: false).originText);
    _destinationController = TextEditingController(
        text:
            Provider.of<TextProvider>(context, listen: false).destinationText);
    WidgetsBinding.instance!.addPostFrameCallback((_) {});
  }

  // 검색
  Future<void> _search() async {
    if (_originController.text.isNotEmpty) {
      try {
        final originResult = await fetchSearchResults(_originController.text);

        setState(() {
          startsearchResults = originResult;
          startkeyWord = _originController.text;
          _showSearchResults = true; // 검색 결과가 있을 때에만 빌드토글버튼을 보이도록 설정
        });
        print(
            '=================================================================');
        // print(searchResults);
        //검색결과 화면에 표시 로직 짜기
      } catch (e) {
        print("============= 검색 실패임: $e =============");
      }
    }
    // 초기화 로직 추가
    else {
      setState(() {
        startsearchResults = [];
        startkeyWord = '';
        _showSearchResults = false;
      });
    }
  }

  Future<void> _destinationsearch() async {
    if (_destinationController.text.isNotEmpty) {
      try {
        final destinationResult =
            await fetchSearchResults(_destinationController.text);

        setState(() {
          destinationsearchResults = destinationResult;
          destinationkeyWord = _destinationController.text;
          _showSearchResults = true; // 검색 결과가 있을 때에만 빌드토글버튼을 보이도록 설정
        });
        print(
            '=================================================================');
        //검색결과 화면에 표시 로직 짜기
      } catch (e) {
        print("============= 검색 실패임: $e =============");
      }
    }
    // 초기화 로직 추가
    else {
      setState(() {
        destinationsearchResults = [];
        destinationkeyWord = '';
        _showSearchResults = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final appKey = dotenv.env['APP_KEY'];

    return Scaffold(
      appBar: CustomAppBar(title: '길찾기'),
      body: SingleChildScrollView(
        child: Column(
          children: [
            Container(
              color: mainOrange,
              child: Padding(
                padding: const EdgeInsets.symmetric(vertical: 16.0),
                child: Row(
                  children: [
                    SizedBox(width: 20),
                    Expanded(
                      child: Column(
                        children: [
                          TextField(
                            controller: _originController,
                            decoration: InputDecoration(
                                contentPadding:
                                    const EdgeInsets.symmetric(horizontal: 8.0),
                                hintText: '출발지를 입력하세요.',
                                hintStyle: TextStyle(
                                  color: mainGray,
                                  fontSize: 16.0,
                                ),
                                filled: true,
                                fillColor: Colors.white,
                                border: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(8.0),
                                  borderSide: BorderSide.none,
                                )),
                            onChanged: (value) {
                              _search();
                              print(value);
                            },
                          ),
                          SizedBox(
                            height: 8.0,
                          ),
                          TextField(
                            controller: _destinationController,
                            decoration: InputDecoration(
                              contentPadding:
                                  EdgeInsets.symmetric(horizontal: 8.0),
                              hintText: '도착지를 입력하세요.',
                              hintStyle: TextStyle(
                                color: mainGray,
                                fontSize: 16.0,
                              ),
                              filled: true,
                              fillColor: Colors.white,
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(8.0),
                                borderSide: BorderSide.none,
                              ),
                            ),
                            onChanged: (value) {
                              _destinationsearch();
                              print(value);
                            },
                          ),
                        ],
                      ),
                    ),
                    SizedBox(width: 20),
                  ],
                ),
              ),
            ),
            SizedBox(height: 10), // 드롭다운과 위젯 사이의 간격
            Container(
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal, // 가로 스크롤 설정
                child: Row(
                  children: [
                    SizedBox(width: 20),
                    Container(
                      width: 110,
                      height: 40,
                      decoration: BoxDecoration(
                        color: Color(0xffffffff),
                        border: Border.all(
                          color: Colors.grey, // 테두리의 색상을 지정합니다.
                          width: 1.0, // 테두리의 너비를 지정합니다.
                        ),
                        borderRadius:
                            BorderRadius.circular(8.0), // 테두리의 모서리를 둥글게 만듭니다.
                      ),
                      child: Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          // 가운데 정렬합니다.
                          children: [
                            DropdownButtonHideUnderline(
                                child: DropdownButton(
                              value: _selectedVehicles,
                              hint: Text('이동방식'),
                              items: _vehicles
                                  .map((e) => DropdownMenuItem(
                                        value: e,
                                        child: Text(e),
                                      ))
                                  .toList(),
                              onChanged: (value) {
                                setState(() {
                                  _selectedVehicles = value!;
                                });
                              },
                            ))
                          ]),
                    ),
                    SizedBox(width: 10), // 드롭다운 버튼과 버튼 사이 간격 조절
                    ElevatedButton(
                      onPressed: () {
                        _fetchBusDirections();
                        if (_originController.text.isNotEmpty &&
                            _destinationController.text.isNotEmpty &&
                            _selectedVehicles != null) {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => TransitPathMap(
                                initialSearchAddress: _originController.text,
                                initialDestinationSearchAddress:
                                    _destinationController.text,
                                startLat: widget.startLat!,
                                startLon: widget.startLon!,
                                endLat: widget.endLat!,
                                endLon: widget.endLon!,
                                formattedCoordinates: [transitCoordinates],
                                type: _selectedVehicles!, // 선택된 이동 방식 전달
                              ),
                            ),
                          );
                        } else {
                          // 출발지와 도착지 값 중 하나라도 비어있을 경우 경고창 표시
                          showDialog(
                            context: context,
                            builder: (context) => AlertDialog(
                              title: Text('경고'),
                              content: Text('정보를 모두 입력해주세요!'),
                              actions: [
                                TextButton(
                                  onPressed: () => Navigator.of(context).pop(),
                                  child: Text('확인'),
                                ),
                              ],
                            ),
                          );
                        }
                      },
                      style: ElevatedButton.styleFrom(
                          backgroundColor: Color(0xffffffff),
                          // 배경 투명지
                          surfaceTintColor: Color(0xffffffff),
                          side: BorderSide(color: mainOrange, width: 1),
                          // 테두리 오렌지
                          shape: RoundedRectangleBorder(
                            borderRadius:
                                BorderRadius.circular(10), // 테두리 반경 10px
                          ),
                          elevation: 4),
                      child: Text(
                        '대중교통',
                        style: TextStyle(
                          fontSize: 17.0,
                          fontWeight: FontWeight.bold,
                          color: mainOrange,
                        ),
                      ),
                    ),
                    SizedBox(width: 10), // 버튼들 사이 간격 조절
                    ElevatedButton(
                      onPressed: () {
                        _fetchTaxiDirections();
                        if (_originController.text.isNotEmpty &&
                            _destinationController.text.isNotEmpty &&
                            _selectedVehicles != null) {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => TaxiPathMap(
                                initialSearchAddress: _originController.text,
                                initialDestinationSearchAddress:
                                    _destinationController.text,
                                startLat: widget.startLat!,
                                startLon: widget.startLon!,
                                endLat: widget.endLat!,
                                endLon: widget.endLon!,
                                type: _selectedVehicles!,
                                formattedCoordinates: [taxiCoordinates],
                                taxiInfo: [taxiInfo],
                                minCost: minCost,
                                maxCost: maxCost,
                                totalDistance: totalDistance,
                                totalTime: totalTime,
                              ),
                            ),
                          );
                        } else {
                          // 출발지와 도착지 값 중 하나라도 비어있을 경우 경고창 표시
                          showDialog(
                            context: context,
                            builder: (context) => AlertDialog(
                              title: Text('경고'),
                              content: Text('정보를 모두 입력해주세요!'),
                              actions: [
                                TextButton(
                                  onPressed: () => Navigator.of(context).pop(),
                                  child: Text('확인'),
                                ),
                              ],
                            ),
                          );
                        }
                      },
                      style: ElevatedButton.styleFrom(
                          backgroundColor: Color(0xffffffff),
                          // 배경 투명지
                          surfaceTintColor: Color(0xffffffff),
                          side: BorderSide(color: mainOrange, width: 1),
                          // 테두리 오렌지
                          shape: RoundedRectangleBorder(
                            borderRadius:
                                BorderRadius.circular(10), // 테두리 반경 10px
                          ),
                          elevation: 4),
                      child: Text(
                        '택시',
                        style: TextStyle(
                          fontSize: 17.0,
                          fontWeight: FontWeight.bold,
                          color: mainOrange,
                        ),
                      ),
                    ),
                    SizedBox(width: 10), // 버튼들 사이 간격 조절
                    ElevatedButton(
                      onPressed: () {
                        // print(wheelDirections);
                        _fetchWheelDirections();
                        if (_originController.text.isNotEmpty &&
                            _destinationController.text.isNotEmpty &&
                            _selectedVehicles != null) {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => WheelPathMap(
                                initialSearchAddress: _originController.text,
                                initialDestinationSearchAddress:
                                    _destinationController.text,
                                startLat: widget.startLat!,
                                startLon: widget.startLon!,
                                endLat: widget.endLat!,
                                endLon: widget.endLon!,
                                type: _selectedVehicles!,
                                formattedCoordinates: [wheelCoordinates],
                                wheelDirections: wheelDirections,
                                totalDistance: totalDistance,
                                totalTime: totalTime,
                                recommendedpathCoordinates:
                                    recommendedPathCoordinates,
                              ),
                            ),
                          );
                        } else {
                          // 출발지와 도착지 값 중 하나라도 비어있을 경우 경고창 표시
                          showDialog(
                            context: context,
                            builder: (context) => AlertDialog(
                              title: Text('경고'),
                              content: Text('정보를 모두 입력해주세요!'),
                              actions: [
                                TextButton(
                                  onPressed: () => Navigator.of(context).pop(),
                                  child: Text('확인'),
                                ),
                              ],
                            ),
                          );
                        }
                      },
                      style: ElevatedButton.styleFrom(
                          backgroundColor: Color(0xffffffff),
                          // 배경 투명지
                          surfaceTintColor: Color(0xffffffff),
                          side: BorderSide(color: mainOrange, width: 1),
                          // 테두리 오렌지
                          shape: RoundedRectangleBorder(
                            borderRadius:
                                BorderRadius.circular(10), // 테두리 반경 10px
                          ),
                          elevation: 4),
                      child: Text(
                        '도보',
                        style: TextStyle(
                          fontSize: 17.0,
                          fontWeight: FontWeight.bold,
                          color: mainOrange,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            SizedBox(height: 10), // 드롭다운과 패널 슬라이딩 사이의 간격TransitPath
            // _buildToggleButton(),
            _showSearchResults
                ? SlidingUpPanel(
                    // controller: _panelController,
                    panel: _buildPanel(), // 검색 결과가 있을 때만 패널을 보이도록 설정
                    // collapsed: _buildCollapsedPanel(),
                    // borderRadius: const BorderRadius.only(
                    //   topRight: Radius.circular(30.0),
                    //   topLeft: Radius.circular(30.
                    // ),
                    minHeight: 100.0,
                    maxHeight: MediaQuery.of(context).size.height * 0.35,
                  )
                : SizedBox(), // 검색 결과가 없으면 아무것도 보이지 않게 함
            Container(
              height: 0.3, // 수평선의 높이 설정
              width: double.infinity, // 수평선이 가로로 전체로 펼쳐지도록 설정
              color: Colors.grey, // 수평선의 색상 설정
            ),
            _TransitList(),
          ],
        ),
      ),
    );
  }

  Widget _TransitList() {
    if (busDirections.isNotEmpty &&
        (_selectedVehicles == '목발' ||
            _selectedVehicles == '휠체어' ||
            _selectedVehicles == '전동휠체어')) {
      return SingleChildScrollView(
        child: Column(
          children: List.generate(
            busDirections.length,
                (i) {
              return Padding(
                padding: EdgeInsets.fromLTRB(30, 30, 30, 0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '${busDirections[i]["pathType"] == 1
                          ? '지하철'
                          : busDirections[i]["pathType"] == 2
                          ? '버스'
                          : '지하철+도보'}', // 여기에 표시할 텍스트 입력
                      style: TextStyle(
                        color: busDirections[i]["pathType"] == 1
                            ? Colors.blue
                            : busDirections[i]["pathType"] == 2
                            ? Colors.green
                            : mainOrange, // 텍스트 색상 설정
                        fontSize: 20, // 텍스트 크기 설정
                        fontWeight: FontWeight.bold, // 텍스트 굵기 설정
                      ),
                    ),
                    SizedBox(height: 10),
                    Row(
                      children: [
                        Text(
                          '${busDirections.isNotEmpty
                              ? busDirections[i]['totalTime']
                              : '?'}분 소요',
                          style: TextStyle(
                            fontSize: 20.0,
                            fontWeight: FontWeight.bold,
                            color: Colors.black,
                          ),
                        ),
                      ],
                    ),
                    SizedBox(height: 20),
                    Stack(
                      children: [
                        Container(
                          height: 20, // 바의 높이
                          width: MediaQuery.of(context).size.width - 60,
                          decoration: BoxDecoration(
                            color: const Color(0xffD3D3D3),
                            borderRadius: BorderRadius.circular(50),
                          ),
                        ),
                        SingleChildScrollView(
                          scrollDirection: Axis.horizontal,
                          child: Row(
                            children: List.generate(
                              busDirections[i]['subPaths'].length,
                                  (j) {
                                return Container(
                                  height: 20,
                                  width: ((MediaQuery.of(context).size.width - 60)) *
                                      busDirections[i]['subPaths'][j]['sectionTime'] /
                                      busDirections[i]['totalTime'],
                                  decoration: BoxDecoration(
                                    color: busDirections[i]["subPaths"][j]["trafficType"] == 1 ? Colors.blue : busDirections[i]["subPaths"][j]["trafficType"] == 2 ? Colors.green : const Color(0xffD3D3D3),
                                    borderRadius: BorderRadius.circular(50),
                                  ),
                                  child: Center(
                                    child: busDirections[i]["subPaths"][j]["trafficType"] == 1
                                        ? Row(
                                      mainAxisAlignment: MainAxisAlignment.center,
                                      children: [
                                        Icon(Icons.directions_subway, color: Colors.white, size: 18),
                                        Flexible(
                                          child: Text(
                                            ' ${busDirections[i]["subPaths"][j]["sectionTime"]}분',
                                            style: TextStyle(color: Colors.white, fontSize: 14),
                                            overflow: TextOverflow.ellipsis,
                                          ),
                                        ),
                                      ],
                                    )
                                        : busDirections[i]["subPaths"][j]["trafficType"] == 2
                                        ? Row(
                                      mainAxisAlignment: MainAxisAlignment.center,
                                      children: [
                                        Icon(Icons.directions_bus, color: Colors.white, size: 18),
                                        Flexible(
                                          child: Text(
                                            ' ${busDirections[i]["subPaths"][j]["sectionTime"]}분',
                                            style: TextStyle(color: Colors.white, fontSize: 14),
                                            overflow: TextOverflow.ellipsis,
                                          ),
                                        ),
                                      ],
                                    )
                                        : Center(
                                      child: Flexible(
                                        child: Text(
                                          ' ${busDirections[i]["subPaths"][j]["sectionTime"]}분',
                                          style: TextStyle(color: Colors.white, fontSize: 14),
                                          overflow: TextOverflow.ellipsis,
                                        ),
                                      ),
                                    ), // 다른 경우에는 아무것도 표시하지 않음
                                  ),
                                );
                              },
                            ),
                          ),
                        ),
                      ],
                    ),
                    SizedBox(height: 20),
                    Column(
                      children: List.generate(
                        busDirections[i]['subPaths'].length,
                            (j) {
                          return Container(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Row(
                                  children: [
                                    // trafficType에 따라 아이콘 및 텍스트를 다르게 생성
                                    busDirections[i]['subPaths'][j]["trafficType"] == 1
                                        ? Container(
                                      child: Column(
                                        crossAxisAlignment: CrossAxisAlignment.start,
                                        children: [
                                          Row(
                                            children: [
                                              Icon(
                                                Icons.directions_subway, // 지하철 아이콘
                                                color: Colors.blue, // 아이콘 색상
                                                size: 22, // 아이콘 크기
                                              ),
                                              SizedBox(width: 5), // 아이콘과 텍스트 사이 간격
                                              Text(
                                                '지하철', // 텍스트 내용
                                                style: TextStyle(fontSize: 16, color: Colors.blue, fontWeight: FontWeight.bold), // 텍스트 스타일
                                              ),
                                              SizedBox(width: 10), // 아이콘과 텍스트 사이 간격
                                              Text(
                                                '${busDirections[i]['subPaths'][j]["startStationName"]}역 승차', // 텍스트 내용
                                                style: TextStyle(fontSize: 16, color: Colors.black, fontWeight: FontWeight.bold), // 텍스트 스타일
                                              ),
                                            ],
                                          ),
                                          Row(
                                            children: [
                                              SizedBox(width: 10),
                                              Container(
                                                height: 50, // 선의 높이
                                                width: 2, // 선의 너비
                                                color: Colors.blue, // 선의 색상
                                              ),
                                              SizedBox(width: 80),
                                              Text(
                                                '${busDirections[i]['subPaths'][j]["stationCount"]} 정거장 이동', // 텍스트 내용
                                                style: TextStyle(fontSize: 16, color: Colors.black), // 텍스트 스타일
                                              ),
                                              // Text(
                                              //   '${busDirections[i]['subPaths'][j]["waitTime"]} 분 남음', // 텍스트 내용
                                              //   style: TextStyle(fontSize: 16, color: Colors.black), // 텍스트 스타일
                                              // ),
                                            ],
                                          ),
                                          Row(
                                            children: [
                                              Icon(
                                                Icons.directions_subway, // 지하철 아이콘
                                                color: Colors.blue, // 아이콘 색상
                                                size: 22, // 아이콘 크기
                                              ),
                                              SizedBox(width: 8), // 아이콘과 텍스트 사이 간격
                                              Text(
                                                '지하철', // 텍스트 내용
                                                style: TextStyle(fontSize: 16, color: Colors.blue, fontWeight: FontWeight.bold), // 텍스트 스타일
                                              ),
                                              SizedBox(width: 10), // 아이콘과 텍스트 사이 간격
                                              Text(
                                                '${busDirections[i]['subPaths'][j]["endStationName"]}역 하차', // 텍스트 내용
                                                style: TextStyle(fontSize: 16, color: Colors.black, fontWeight: FontWeight.bold), // 텍스트 스타일
                                              ),
                                            ],
                                          ),
                                        ],
                                      ),
                                    )
                                        : busDirections[i]['subPaths'][j]["trafficType"] == 2
                                        ? Container(
                                      child: Column(
                                        crossAxisAlignment: CrossAxisAlignment.start,
                                        children: [
                                          Row(
                                            children: [
                                              Icon(
                                                Icons.directions_bus, // 버스 아이콘
                                                color: Colors.green, // 아이콘 색상
                                                size: 22, // 아이콘 크기
                                              ),
                                              SizedBox(width: 5), // 아이콘과 텍스트 사이 간격
                                              Text(
                                                '저상', // 텍스트 내용
                                                style: TextStyle(fontSize: 16, color: Colors.green, fontWeight: FontWeight.bold), // 텍스트 스타일
                                              ),
                                              SizedBox(width: 22), // 아이콘과 텍스트 사이 간격
                                              Text(
                                                '${busDirections[i]['subPaths'][j]["startStationName"]} 정류장 승차', // 텍스트 내용
                                                style: TextStyle(fontSize: 16, color: Colors.black, fontWeight: FontWeight.bold), // 텍스트 스타일
                                              ),
                                            ],
                                          ),
                                          Row(
                                            children: [
                                              SizedBox(width: 10),
                                              Container(
                                                height: 50, // 선의 높이
                                                width: 2, // 선의 너비
                                                color: Colors.green, // 선의 색상
                                              ),
                                              SizedBox(width: 10),
                                              Row(
                                                children: [
                                                  SizedBox(width: 70),
                                                  Container(
                                                    padding: EdgeInsets.symmetric(vertical: 1, horizontal: 7), // 텍스트 주위의 간격 조정
                                                    decoration: BoxDecoration(
                                                      color: Colors.green, // 배경색 설정
                                                      borderRadius: BorderRadius.circular(50), // 보더 라디우스 설정
                                                    ),
                                                    child: Text(
                                                      '${busDirections[i]['subPaths'][j]["busList"][0]["busNo"]}', // 텍스트 내용
                                                      style: TextStyle(fontSize: 16, color: Colors.white, fontWeight: FontWeight.bold), // 텍스트 스타일
                                                    ),
                                                  ),
                                                  SizedBox(width: 5),
                                                  Text(
                                                    '${busDirections[i]['subPaths'][j]["busList"][0]["busBeforeCount"]} 정거장 전', // 텍스트 내용
                                                    style: TextStyle(fontSize: 16, color: Colors.black), // 텍스트 스타일
                                                  ),
                                                ],
                                              ),
                                            ],
                                          ),
                                          Row(
                                            children: [
                                              Icon(
                                                Icons.directions_bus, // 버스 아이콘
                                                color: Colors.green, // 아이콘 색상
                                                size: 22, // 아이콘 크기
                                              ),
                                              SizedBox(width: 5), // 아이콘과 텍스트 사이 간격
                                              Text(
                                                '저상', // 텍스트 내용
                                                style: TextStyle(fontSize: 16, color: Colors.green, fontWeight: FontWeight.bold), // 텍스트 스타일
                                              ),
                                              SizedBox(width: 22), // 아이콘과 텍스트 사이 간격
                                              Text(
                                                '${busDirections[i]['subPaths'][j]["endStationName"]} 정류장 하차', // 텍스트 내용
                                                style: TextStyle(fontSize: 16, color: Colors.black, fontWeight: FontWeight.bold), // 텍스트 스타일
                                              ),
                                            ],
                                          ),
                                        ],
                                      ),
                                    )
                                        : Padding(
                                      padding: EdgeInsets.symmetric(vertical: 10, horizontal: 0),
                                      child: Row(
                                        children: [
                                          Icon(
                                            Icons.accessible, // 걷는 아이콘
                                            color: Colors.grey, // 아이콘 색상
                                            size: 22, // 아이콘 크기
                                          ),
                                          SizedBox(width: 5), // 아이콘과 텍스트 사이 간격
                                          Text(
                                            '${_selectedVehicles}', // 텍스트 내용
                                            style: TextStyle(fontSize: 16, color: mainGray, fontWeight: FontWeight.bold), // 텍스트 스타일
                                          ),
                                          SizedBox(width: 10), // 아이콘과 텍스트 사이 간격
                                          Text(
                                            '${busDirections[i]['subPaths'][j]["sectionDistance"]}m 이동,', // 텍스트 내용
                                            style: TextStyle(fontSize: 16, color: Colors.black), // 텍스트 스타일
                                          ),
                                          SizedBox(width: 10), // 아이콘과 텍스트 사이 간격
                                          Text(
                                            '${busDirections[i]['subPaths'][j]["sectionTime"]}분 소요', // 텍스트 내용
                                            style: TextStyle(fontSize: 16, color: Colors.black), // 텍스트 스타일
                                          ),
                                        ],
                                      ),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                    ),
                    SizedBox(height: 20),
                    Divider(),
                  ],
                ),
              );
            },
          ),
        ),
      );
    } else {
      return SizedBox(); // 아무것도 표시하지 않음
    }
  }

  Widget _buildPanel() {
    return DirectionSearchResultList(
      searchResults: [], // 빈 리스트로 전달해도 됨
      startsearchResults: startsearchResults, // 빈 리스트로 전달해도 됨
      destinationsearchResults: destinationsearchResults,
    );
  }
}
