import 'package:barrier_free/component/appBar.dart';
import 'package:flutter/material.dart';
import 'package:barrier_free/const/color.dart';
import 'package:flutter/services.dart';

class PlaceDetailScreen extends StatefulWidget {
  const PlaceDetailScreen({super.key});

  @override
  State<PlaceDetailScreen> createState() => _PlaceDetailScreenState();
}

class _PlaceDetailScreenState extends State<PlaceDetailScreen> {
  static const platform = MethodChannel('com.example.barrier_free/tmap');

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('장소 상세'),
      ),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 20.0, horizontal: 30.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Text(
                    '삼성화재 유성연수원',
                    style: TextStyle(
                      fontSize: 20.0,
                      fontWeight: FontWeight.bold,
                      color: mainBlack,
                    ),
                  ),
                  SizedBox(width: 10.0), // 공백
                  Text(
                    '편의 시설',
                    style: TextStyle(
                      fontSize: 12.0,
                      color: mainGray,
                    ),
                  ),
                ],
              ),
              SizedBox(height: 10.0),
              Row(
                children: [
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
                      '출발',
                      style: TextStyle(
                        fontSize: 18.0,
                        fontWeight: FontWeight.bold,
                        color: mainOrange,
                      ),
                    ),
                  ),
                  SizedBox(width: 10.0),
                  ElevatedButton(
                    onPressed: () {},
                    style: ElevatedButton.styleFrom(
                      backgroundColor: mainOrange,
                      side: BorderSide(color: mainOrange, width: 1), // 테두리 오렌지
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10), // 테두리 반경 10px
                      ),
                    ),
                    child: Text(
                      '도착',
                      style: TextStyle(
                        fontSize: 18.0,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
                  ),
                ],
              ),
              SizedBox(height: 20.0),
              // 지도 위젯
              Container(
                width: MediaQuery.of(context).size.width*0.9,
                height: MediaQuery.of(context).size.width*0.9,
                child: AndroidView(
                  viewType: 'showTMap',
                  creationParams: {},
                  creationParamsCodec: StandardMessageCodec(),
                ),
              ),
              SizedBox(height: 20.0),
              Row(
                children: [
                  Icon(
                    Icons.location_on,
                    color: mainGray,
                    size: 16.0,
                  ),
                  SizedBox(width: 5.0),
                  Text(
                    '대전 서구 문정로',
                    style: TextStyle(
                      fontSize: 14.0,
                      color: mainGray,
                    ),
                  ),
                ],
              ),
              SizedBox(height: 10.0),
              Row(
                children: [
                  Icon(
                    Icons.access_time,
                    color: mainGray,
                    size: 16.0,
                  ),
                  SizedBox(width: 5.0),
                  Text(
                    '이용시간',
                    style: TextStyle(
                      fontSize: 14.0,
                      color: mainGray,
                    ),
                  ),
                ],
              ),
              SizedBox(height: 10.0),
              Row(
                children: [
                  Icon(
                    Icons.phone_in_talk,
                    color: mainGray,
                    size: 16.0,
                  ),
                  SizedBox(width: 5.0),
                  Text(
                    '1588-1588',
                    style: TextStyle(
                      fontSize: 14.0,
                      color: mainGray,
                    ),
                  ),
                ],
              ),
              // SizedBox(height: 10.0),
              // Row(
              //   children: [
              //     Icon(
              //       Icons.access_time,
              //       color: mainGray,
              //       size: 16.0,
              //     ),
              //     SizedBox(width: 5.0),
              //     Text(
              //       'URL',
              //       style: TextStyle(
              //         fontSize: 14.0,
              //         color: mainGray,
              //       ),
              //     ),
              //   ],
              // ),
              SizedBox(height: 20.0),
              Text(
                '베리어프리 시설',
                style: TextStyle(
                  fontSize: 20.0,
                  fontWeight: FontWeight.bold,
                  color: mainBlack,
                ),
              ),
              SizedBox(height: 10.0),
              Row(
                children: [
                  ElevatedButton(
                    onPressed: () {},
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Color(0xffffffff), // 배경 투명
                      side: BorderSide(color: mainOrange, width: 1), // 테두리 오렌지
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10), // 테두리 반경 10px
                      ),
                    ),
                    child: Text(
                      '엘레베이터',
                      style: TextStyle(
                        fontSize: 18.0,
                        color: mainBlack,
                      ),
                    ),
                  ),
                  // SizedBox(width: 10.0),
                ],
              ),
              SizedBox(height: 20.0),
              Text(
                '방문자 리뷰',
                style: TextStyle(
                  fontSize: 20.0,
                  fontWeight: FontWeight.bold,
                  color: mainBlack,
                ),
              ),
              SizedBox(height: 20.0),
              Row(
                children: [
                  CircleAvatar(
                    // 사용자 프로필 이미지
                    backgroundImage: AssetImage('assets/images/profile_image.png'),
                    radius: 20, // 원의 반지름
                  ),
                  SizedBox(width: 10), // 간격
                  Text(
                    '민지',
                    style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
              SizedBox(height: 10.0), // 간격
              Row(
                children: [
                  // Image.asset('assets/images/attached_image.png'),
                  Container(
                    width: 130, // 원하는 너비
                    height: 130, // 원하는 높이
                    color: mainOrange, // 흰색 배경
                  ),
                  SizedBox(width: 20), // 간격
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        ElevatedButton(
                          onPressed: () {},
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Color(0xffffffff), // 배경 투명
                            side: BorderSide(color: mainOrange, width: 1), // 테두리 오렌지
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(10), // 테두리 반경 10px
                            ),
                          ),
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Text(
                                '엘레베이터',
                                style: TextStyle(
                                  fontSize: 18.0,
                                  color: mainBlack,
                                ),
                              ),
                              SizedBox(width: 10),
                              Icon(Icons.thumb_up_alt_outlined, color: mainOrange), // 아이콘
                            ],
                          ),
                        ),
                        Padding(
                          padding: const EdgeInsets.symmetric(vertical: 10.0, horizontal: 5.0),
                          child: Text(
                            '엘레베이터가 있긴 한데, 휠체어가 들어가지 않네요.',
                            style: TextStyle(
                              fontSize: 16,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
              ElevatedButton(
                onPressed: () {
                  // 버튼이 눌렸을 때 수행할 동작 추가
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: mainOrange, // 배경 투명
                  side: BorderSide(color: mainOrange, width: 1), // 테두리 오렌지
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10), // 테두리 반경 10px
                  ),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      '리뷰 더보기',
                      style: TextStyle(
                        fontSize: 18.0,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
