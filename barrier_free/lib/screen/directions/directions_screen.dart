import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/screen/directions/directions_mapresult_screen.dart';
import 'package:barrier_free/services/search_service.dart';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';

class DirectionsScreen extends StatefulWidget {
  const DirectionsScreen({super.key});

  @override
  State<DirectionsScreen> createState() => _DirectionsScreenState();
}

class _DirectionsScreenState extends State<DirectionsScreen> {
  final TextEditingController _originController = TextEditingController();
  final TextEditingController _destinationController = TextEditingController();

  final _vehicles = ['목발', '휠체어', '전동휠체어'];
  String? _selectedVehicles;
  Position? _currentPosition;

  @override
  void initState() {
    super.initState();
    setState(() {
      _selectedVehicles = _vehicles[0];
    });
  }

  // 검색
  Future<void> _search() async {
    if (_originController.text.isNotEmpty) {
      try {
        final result = await fetchSearchResults(
          _originController.text,
        );
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => DirectionsMapResultScreen(
                searchResults: result, keyWord: _originController.text),
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

  @override
  Widget build(BuildContext context) {
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
                              onPressed: _search,
                              icon: Icon(
                                Icons.search,
                              ),
                            ),
                          ),
                          onChanged: (value) {
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
                              onPressed: _search,
                              icon: Icon(
                                Icons.search,
                              ),
                            ),
                          ),
                          onChanged: (value) {
                            print(value);
                          },
                        ),
                      ],
                    ),
                  ),
                  IconButton(
                    onPressed: () {},
                    icon: Icon(
                      Icons.check,
                      color: Colors.white,
                      size: 40.0,
                    ),
                    splashColor: Colors.transparent,
                    highlightColor: Colors.transparent,
                  ),
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
                    onPressed: () {},
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
                    onPressed: () {},
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
                    onPressed: () {},
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
        ],
      ),
    );
  }
}
