import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/provider/user_provider.dart';
import 'package:barrier_free/screen/review/review_screen.dart';
import 'package:barrier_free/services/bookmarkPlace_service.dart';
import 'package:barrier_free/services/review_service.dart';
import 'package:barrier_free/services/secure_storage_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:kakaomap_webview/kakaomap_webview.dart';
import 'package:provider/provider.dart';

class PlaceDetailScreen extends StatefulWidget {
  final Map<String, dynamic> placeDetail;
  final String placeCategory;

  const PlaceDetailScreen({
    super.key,
    required this.placeDetail,
    required this.placeCategory,
  });

  @override
  State<PlaceDetailScreen> createState() => _PlaceDetailScreenState();
}

class _PlaceDetailScreenState extends State<PlaceDetailScreen> {
  int? userId;
  bool isStarFilled = false;
  late Future<List<dynamic>> reviewListFuture;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _initializeUserId();
    reviewListFuture =
        ReviewService().fetchReviewByPlaceId(widget.placeDetail['id']);
    print(widget.placeDetail['id']);
  }

  Future<void> _initializeUserId() async {
    String? token = await SecureStorageService().getToken();
    if (token != null) {
      int decodeUserId = await SecureStorageService().decodeToken(token);
      setState(() {
        userId = decodeUserId;
      });
      print(decodeUserId);
    }
  }

  @override
  Widget build(BuildContext context) {
    final appKey = dotenv.env['APP_KEY'];
    final isLoggedIn = Provider.of<UserProvider>(context).isLoggedIn();

    return Scaffold(
      appBar: CustomAppBar(title: '장소 상세'),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 20.0, horizontal: 30.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Text(
                    widget.placeDetail['place_name'],
                    style: TextStyle(
                      fontSize: 20.0,
                      fontWeight: FontWeight.bold,
                      color: mainBlack,
                    ),
                  ),
                  SizedBox(width: 10.0), // 공백
                  Text(
                    widget.placeCategory,
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
                  Spacer(),
                  isLoggedIn
                      ? IconButton(
                          onPressed: () {
                            bookmarkPlaceService().bookmarkPlace(
                                userId!,
                                widget.placeDetail['id'],
                                widget.placeDetail['place_name'],
                                widget.placeDetail['address_name'],
                                widget.placeDetail['y'],
                                widget.placeDetail['x']);
                            setState(() {
                              isStarFilled = !isStarFilled;
                            });
                          },
                          icon: isStarFilled
                              ? Icon(
                                  Icons.star,
                                  color: mainOrange,
                                  size: 30.0,
                                )
                              : Icon(
                                  Icons.star_border,
                                  color: mainOrange,
                                  size: 30.0,
                                ),
                        )
                      : Container(),
                ],
              ),
              SizedBox(height: 20.0),
              // 지도 위젯
              Container(
                width: MediaQuery.of(context).size.width * 0.9,
                height: MediaQuery.of(context).size.width * 0.9,
                child: KakaoMapView(
                  width: MediaQuery.of(context).size.width * 0.7,
                  height: MediaQuery.of(context).size.height * 0.7,
                  kakaoMapKey: appKey!,
                  lat: double.tryParse(widget.placeDetail['y'].toString()) ??
                      0.0,
                  lng: double.tryParse(widget.placeDetail['x'].toString()) ??
                      0.0,
                  showZoomControl: false,
                  showMapTypeControl: false,
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
                    widget.placeDetail['road_address_name'],
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
                    Icons.web_asset_outlined,
                    color: mainGray,
                    size: 16.0,
                  ),
                  SizedBox(width: 5.0),
                  Text(
                    widget.placeDetail['place_url'],
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
                    widget.placeDetail['phone'],
                    style: TextStyle(
                      fontSize: 14.0,
                      color: mainGray,
                    ),
                  ),
                ],
              ),
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
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    '방문자 리뷰',
                    style: TextStyle(
                      fontSize: 20.0,
                      fontWeight: FontWeight.bold,
                      color: mainBlack,
                    ),
                  ),
                  ElevatedButton(
                      onPressed: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => ReviewScreen(poiId: widget.placeDetail['id']),
                          ),
                        );
                      },
                      child: Text('리뷰 작성하기')
                  ),
                ],
              ),
              SizedBox(height: 20.0),
              FutureBuilder<List<dynamic>>(
                  future: reviewListFuture,
                  builder: (context, snapshot) {
                    if (snapshot.connectionState == ConnectionState.waiting) {
                      return Center(
                        child: Text(
                            '${widget.placeDetail['place_name']}의 리뷰를 불러오고 있습니다.'),
                      );
                    } else if (snapshot.hasError) {
                      return Column(
                        children: [
                          Text('리뷰를 불러오는데 실패했습니다 :${snapshot.error}'),
                        ],
                      );
                    } else if (snapshot.hasData && snapshot.data!.isNotEmpty) {
                      return ListView.builder(
                        physics: NeverScrollableScrollPhysics(), // 스크롤 중첩 문제 해결
                        shrinkWrap: true, // 내부 ListView 스크롤 가능하도록
                        itemCount: snapshot.data!.length,
                        itemBuilder: (context, index) {
                          var review = snapshot.data![index];
                          return Text('리뷰리뷰리뷰'); // Custom Review Widget
                        },
                      );
                    } else {
                      return Center(
                        child: Text(
                          '리뷰가 없습니다.',
                          style: TextStyle(fontSize: 16.0),
                        ),
                      );
                    }
                  }),
              SizedBox(height: 10.0), // 간격
              Row(
                children: [
                  // // Image.asset('assets/images/attached_image.png'),
                  // Container(
                  //   width: 130, // 원하는 너비
                  //   height: 130, // 원하는 높이
                  //   color: mainOrange, // 흰색 배경
                  // ),
                  // SizedBox(width: 20), // 간격
                  // Expanded(
                  //   child: Column(
                  //     crossAxisAlignment: CrossAxisAlignment.stretch,
                  //     children: [
                  //       ElevatedButton(
                  //         onPressed: () {},
                  //         style: ElevatedButton.styleFrom(
                  //           backgroundColor: Color(0xffffffff),
                  //           // 배경 투명
                  //           side: BorderSide(color: mainOrange, width: 1),
                  //           // 테두리 오렌지
                  //           shape: RoundedRectangleBorder(
                  //             borderRadius:
                  //                 BorderRadius.circular(10), // 테두리 반경 10px
                  //           ),
                  //         ),
                  //         child: Row(
                  //           mainAxisAlignment: MainAxisAlignment.center,
                  //           children: [
                  //             Text(
                  //               '엘레베이터',
                  //               style: TextStyle(
                  //                 fontSize: 18.0,
                  //                 color: mainBlack,
                  //               ),
                  //             ),
                  //             SizedBox(width: 10),
                  //             Icon(Icons.thumb_up_alt_outlined,
                  //                 color: mainOrange), // 아이콘
                  //           ],
                  //         ),
                  //       ),
                  // Padding(
                  //   padding: const EdgeInsets.symmetric(
                  //       vertical: 10.0, horizontal: 5.0),
                  //   child: Text(
                  //     '엘레베이터가 있긴 한데, 휠체어가 들어가지 않네요.',
                  //     style: TextStyle(
                  //       fontSize: 16,
                  //     ),
                  //   ),
                  // ),
                  //     ],
                  //   ),
                  // ),
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
