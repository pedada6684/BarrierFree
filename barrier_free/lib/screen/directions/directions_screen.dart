import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/providers/text_provider.dart';
import 'package:barrier_free/screen/directions/directionssearch_result_list.dart';
import 'package:barrier_free/screen/directions/taxi_path_map.dart';
import 'package:barrier_free/screen/directions/transit_path_map.dart';
import 'package:barrier_free/screen/directions/wheel_path_map.dart';
import 'package:barrier_free/services/buspath_service.dart';
import 'package:barrier_free/services/search_service.dart';
import 'package:barrier_free/services/taxipath_service.dart';
import 'package:barrier_free/services/wheelpath_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:geolocator/geolocator.dart';
import 'package:provider/provider.dart';
import 'package:sliding_up_panel/sliding_up_panel.dart';

class DirectionsScreen extends StatefulWidget {
  final String? initialSearchAddress;
  final String? initialDestinationSearchAddress;

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
  });

  @override
  State<DirectionsScreen> createState() => _DirectionsScreenState(
  );
}

class _DirectionsScreenState extends State<DirectionsScreen> {
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

  late String transitCoordinates = '';
  late String wheelCoordinates = '';
  late String taxiCoordinates = '';

  void _fetchTaxiDirections() async {
    try {
      final taxiDirections = await TaxiPathService().fetchTaxiDirectionsResults(
        type: '휠체어',
        startLat: widget.startLat!,
        startLon: widget.startLon!,
        endLat: widget.endLat!,
        endLon: widget.endLon!,
      );

      // 택시 경로에서 좌표 데이터를 가져와서 포맷합니다.
      List<String> coordinatesList = taxiDirections.map((direction) =>
      'new kakao.maps.LatLng(${direction['latitude']}, ${direction['longitude']})').toList();
      String newFormattedCoordinates = '[${coordinatesList.join(', ')}]';

      setState(() {
        taxiCoordinates = newFormattedCoordinates; // 포맷된 좌표 데이터를 상태에 저장합니다.
        print('===============택시===================');
        print('formattedCoordinates = ${taxiCoordinates}');
      });
    } catch (e) {
      print('Error fetching taxi directions: $e');
      // 에러 처리
    }
  }

  void _fetchWheelDirections() async {
    try {
      final wheelDirections = await WheelPathService().fetchWheelDirectionsResults(
        type: '휠체어',
        startLat: widget.startLat!,
        startLon: widget.startLon!,
        endLat: widget.endLat!,
        endLon: widget.endLon!,
      );

      // 택시 경로에서 좌표 데이터를 가져와서 포맷합니다.
      List<String> coordinatesList = wheelDirections.map((direction) =>
      'new kakao.maps.LatLng(${direction['latitude']}, ${direction['longitude']})').toList();
      String newFormattedCoordinates = '[${coordinatesList.join(', ')}]';

      setState(() {
        wheelCoordinates = newFormattedCoordinates; // 포맷된 좌표 데이터를 상태에 저장합니다.
        print('===============휠체어===================');
        print('formattedCoordinates = ${wheelCoordinates}');
      });
    } catch (e) {
      print('=========================================');
      print('Error fetching wheel directions: $e');
      // 에러 처리
    }
  }

  void _fetchBusDirections() async {
    try {
      final busDirections = await BusPathService().fetchBusDirectionsResults(
        type: '휠체어',
        startLat: widget.startLat!,
        startLon: widget.startLon!,
        endLat: widget.endLat!,
        endLon: widget.endLon!,
      );

      // 택시 경로에서 좌표 데이터를 가져와서 포맷합니다.
      List<String> coordinatesList = busDirections.map((direction) =>
      'new kakao.maps.LatLng(${direction['latitude']}, ${direction['longitude']})').toList();
      String newFormattedCoordinates = '[${coordinatesList.join(', ')}]';

      setState(() {
        transitCoordinates = newFormattedCoordinates; // 포맷된 좌표 데이터를 상태에 저장합니다.
        print('===============버스===================');
        print('formattedCoordinates = ${transitCoordinates}');
      });
    } catch (e) {
      print('=========================================');
      print('Error fetching wheel directions: $e');
      // 에러 처리
    }
  }

  @override
  void initState() {
    super.initState();

    _fetchWheelDirections();
    _fetchBusDirections();
    _fetchTaxiDirections();
    _originController = TextEditingController(
        text: Provider.of<TextProvider>(context, listen: false).originText);
    _destinationController = TextEditingController(
        text: Provider.of<TextProvider>(context, listen: false).destinationText);
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
        final destinationResult = await fetchSearchResults(_destinationController.text);

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
      body: Column(
        children: [
          Container(
            color: mainOrange,
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 16.0),
              child: Row(
                children: [
                  IconButton(
                    onPressed: () {},
                    icon: Icon(
                      Icons.swap_vert,
                      color: Colors.white,
                      size: 40.0,
                    ),
                    splashColor: Colors.transparent,
                    highlightColor: Colors.transparent,
                  ),
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
                              fontSize: 18.0,
                            ),
                            filled: true,
                            fillColor: Colors.white,
                            border: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(8.0),
                              borderSide: BorderSide.none,
                            ),
                            suffixIcon: IconButton(
                              onPressed: () {
                                // _panelController.isPanelClosed
                                //     ? _panelController.open()
                                //     : _panelController.close();
                                },
                              icon: Icon(
                                Icons.search,
                              ),
                            ),
                          ),
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
                              fontSize: 18.0,
                            ),
                            filled: true,
                            fillColor: Colors.white,
                            border: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(8.0),
                              borderSide: BorderSide.none,
                            ),
                            suffixIcon: IconButton(
                              onPressed: () {},
                              icon: Icon(Icons.search,),
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
                  SizedBox(width: 50),
                ],
              ),
            ),
          ),
          SizedBox(height: 10), // 드롭다운과 위젯 사이의 간격
          Container(
            // alignment: Alignment.center,
            child: SingleChildScrollView(
              scrollDirection: Axis.horizontal, // 가로 스크롤 설정
              child: Row(
                children: [
                  SizedBox(width: 20),
                  DropdownButton(
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
                  ),
                  SizedBox(width: 10), // 드롭다운 버튼과 버튼 사이 간격 조절
                  ElevatedButton(
                    onPressed: () {
                      if (_originController.text.isNotEmpty &&
                          _destinationController.text.isNotEmpty &&
                          _selectedVehicles != null) {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => TransitPathMap(
                              initialSearchAddress: _originController.text,
                              initialDestinationSearchAddress: _destinationController.text,
                              startLat: widget.startLat!,
                              startLon: widget.startLon!,
                              endLat: widget.endLat!,
                              endLon: widget.endLon!,
                              vehicleType: _selectedVehicles!, // 선택된 이동 방식 전달
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
                      backgroundColor: Color(0xffffffff), // 배경 투명지
                      side: BorderSide(color: mainOrange, width: 1), // 테두리 오렌지
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10), // 테두리 반경 10px
                      ),
                    ),
                    child: Text(
                      '대중교통',
                      style: TextStyle(
                        fontSize: 18.0,
                        fontWeight: FontWeight.bold,
                        color: mainOrange,
                      ),
                    ),
                  ),
                  SizedBox(width: 10), // 버튼들 사이 간격 조절
                  ElevatedButton(
                    onPressed: () {
                      if (_originController.text.isNotEmpty &&
                          _destinationController.text.isNotEmpty &&
                          _selectedVehicles != null) {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => TaxiPathMap(
                              initialSearchAddress: _originController.text,
                              initialDestinationSearchAddress: _destinationController.text,
                              startLat: widget.startLat!,
                              startLon: widget.startLon!,
                              endLat: widget.endLat!,
                              endLon: widget.endLon!,
                              vehicleType: _selectedVehicles!,
                              formattedCoordinates: [taxiCoordinates],
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
                      backgroundColor: Color(0xffffffff), // 배경 투명지
                      side: BorderSide(color: mainOrange, width: 1), // 테두리 오렌지
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10), // 테두리 반경 10px
                      ),
                    ),
                    child: Text(
                      '택시',
                      style: TextStyle(
                        fontSize: 18.0,
                        fontWeight: FontWeight.bold,
                        color: mainOrange,
                      ),
                    ),
                  ),
                  SizedBox(width: 10), // 버튼들 사이 간격 조절
                  ElevatedButton(
                    onPressed: () {
                      if (_originController.text.isNotEmpty &&
                          _destinationController.text.isNotEmpty &&
                          _selectedVehicles != null) {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => WheelPathMap(
                              initialSearchAddress: _originController.text,
                              initialDestinationSearchAddress: _destinationController.text,
                              startLat: widget.startLat!,
                              startLon: widget.startLon!,
                              endLat: widget.endLat!,
                              endLon: widget.endLon!,
                              vehicleType: _selectedVehicles!,
                              formattedCoordinates: [wheelCoordinates],
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
                      backgroundColor: Color(0xffffffff), // 배경 투명지
                      side: BorderSide(color: mainOrange, width: 1), // 테두리 오렌지
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10), // 테두리 반경 10px
                      ),
                    ),
                    child: Text(
                      '도보',
                      style: TextStyle(
                        fontSize: 18.0,
                        fontWeight: FontWeight.bold,
                        color: mainOrange,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          SizedBox(height: 10), // 드롭다운과 패널 슬라이딩 사이의 간격
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
        ],
      ),
    );
  }

  Widget _buildPanel() {
    return DirectionSearchResultList(
      searchResults: [], // 빈 리스트로 전달해도 됨
      startsearchResults: startsearchResults, // 빈 리스트로 전달해도 됨
      destinationsearchResults: destinationsearchResults,
    );
  }
}
