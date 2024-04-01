import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/provider/user_provider.dart';
import 'package:barrier_free/screen/review/review_screen.dart';
import 'package:barrier_free/services/bookmarkPlace_service.dart';
import 'package:barrier_free/services/place_service.dart';
import 'package:barrier_free/services/review_service.dart';
import 'package:barrier_free/services/secure_storage_service.dart';
import 'package:barrier_free/services/test_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:kakaomap_webview/kakaomap_webview.dart';
import 'package:barrier_free/screen/directions/directions_screen.dart';
import 'package:provider/provider.dart';
import 'package:barrier_free/providers/text_provider.dart';

class PlaceDetailScreen extends StatefulWidget {
  final Map<String, dynamic> placeDetail;
  final String placeCategory;
  final bool isStart;
  
  const PlaceDetailScreen(
      {super.key,
      required this.placeDetail,
      required this.placeCategory,
      required this.isStart});

  @override
  State<PlaceDetailScreen> createState() => _PlaceDetailScreenState();
}

class _PlaceDetailScreenState extends State<PlaceDetailScreen> {
  late double startLat;
  late double startLon;
  late double endLat;
  late double endLon;

  int? userId;
  bool isStarFilled = false;
  late Future<List<dynamic>> reviewListFuture;
  late String barrierFreeCodes;
  late Map<String, String> barrierFreeInfo;
  Map<String, dynamic>? barrierFreeList;
  late Future<Map<String, dynamic>>? barrierFreeDetailsFuture;
  List<String>? barrierFreeDetails;

  @override
  void initState() {
    super.initState();
    // 장소 상세 정보에서 위도와 경도를 가져옵니다.
    if (widget.isStart) {
      startLat = double.tryParse(widget.placeDetail['y'].toString()) ?? 0.0;
      startLon = double.tryParse(widget.placeDetail['x'].toString()) ?? 0.0;
      endLat = Provider.of<TextProvider>(context, listen: false).endLat ?? 0.0;
      endLon = Provider.of<TextProvider>(context, listen: false).endLon ?? 0.0;

      // Provider를 사용하여 위도와 경도 설정
      Provider.of<TextProvider>(context, listen: false).setStartLat(startLat);
      Provider.of<TextProvider>(context, listen: false).setStartLon(startLon);
    } else {
      // 위젯이 생성될 때 도착지의 위도와 경도는 초기화하지 않습니다.
      endLat = double.tryParse(widget.placeDetail['y'].toString()) ?? 0.0;
      endLon = double.tryParse(widget.placeDetail['x'].toString()) ?? 0.0;
      startLat = Provider.of<TextProvider>(context, listen: false).startLat ?? 0.0;
      startLon = Provider.of<TextProvider>(context, listen: false).startLon ?? 0.0;

      Provider.of<TextProvider>(context, listen: false).setEndLat(endLat);
      Provider.of<TextProvider>(context, listen: false).setEndLon(endLon);
    }

    _initializeUserId();
    reviewListFuture =
        ReviewService().fetchReviewByPlaceId(widget.placeDetail['id']);
    barrierFreeDetailsFuture = _fetchBarrierFreeDetails();
    _fetchBarrierFreeDetails().then((details) {
      setState(() {
        barrierFreeDetails = List<String>.from(details['barrierFree']).toList();
      });
    });
  }

  Future<void> _initializeUserId() async {
    String? token = await SecureStorageService().getToken();
    if (token != null) {
      int decodeUserId = await SecureStorageService().decodeToken(token);
      setState(() {
        userId = decodeUserId;
      });
      // print(decodeUserId);
    }
  }

  Future<Map<String, dynamic>> _fetchBarrierFreeDetails() async {
    // Map<String, dynamic> details = {};
    try {
      final details =
      await PlaceService().fetchBfByPoiId(widget.placeDetail['id']);
      return details;
    } catch (e) {
      print('배리어프리 정보 가져오기 실패: $e');
      return {};
    }
  }

  List<Widget> _buildBarrierFreeButtons(List<dynamic> barrierFreeDetailsList) {
    List<Widget> buttons = [];

    for (var detail in barrierFreeDetailsList) {
      buttons.add(
        ElevatedButton(
          onPressed: () {},
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.white,
            side: BorderSide(color: mainOrange, width: 1),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(10),
            ),
          ),
          child: Text(
            detail, // 직접 세부사항을 표시합니다.
            style: TextStyle(fontSize: 16.0, color: mainBlack),
          ),
        ),
      );
      buttons.add(SizedBox(width: 10.0)); // 버튼 사이 간격
    }

    return buttons;
  }

  void refreshReviews() {
    setState(() {
      reviewListFuture =
          ReviewService().fetchReviewByPlaceId(widget.placeDetail['id']);
    });
  }

  @override
  Widget build(BuildContext context) {
    final appKey = dotenv.env['APP_KEY'];
    final provider = Provider.of<TextProvider>(context);
    final isLoggedIn = Provider.of<UserProvider>(context).isLoggedIn();
    final TestService testService = TestService();

    String customScript = """
        var mapContainer = document.getElementById('map');
        var mapOption = {
            center: new kakao.maps.LatLng(${widget.placeDetail['y']}, ${widget.placeDetail['x']}),
            level: 3
        };
        var detailMap = new kakao.maps.Map(mapContainer, mapOption);
        
        // 드래그 비활성화
        detailMap.setDraggable(false);
        
        var markerPosition = new kakao.maps.LatLng(${widget.placeDetail['y']}, ${widget.placeDetail['x']});
        var marker = new kakao.maps.Marker({
            position: markerPosition
        });
        marker.setMap(detailMap);
        
      """;

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
                    onPressed: () {
                      // 출발 버튼이 눌렸을 때 DirectionsScreen으로 이동하며 출발지를 설정합니다.
                      Provider.of<TextProvider>(context, listen: false)
                          .setOriginText(widget.placeDetail['place_name']);
                      Provider.of<TextProvider>(context, listen: false)
                          .setStartLat(startLat);
                      Provider.of<TextProvider>(context, listen: false)
                          .setStartLon(startLon);
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => DirectionsScreen(
                            initialSearchAddress: widget.placeDetail['place_name'],
                            startLat: startLat,
                            startLon: startLon,
                            endLat: endLat,
                            endLon: endLon,
                          ),
                        ),
                      );
                    },
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
                    onPressed: () {
                      // 도착 버튼이 눌렸을 때 DirectionsScreen으로 이동하며 도착지를 설정합니다.
                      Provider.of<TextProvider>(context, listen: false)
                          .setDestinationText(widget.placeDetail['place_name']);
                      Provider.of<TextProvider>(context, listen: false)
                          .setEndLat(endLat);
                      Provider.of<TextProvider>(context, listen: false)
                          .setEndLon(endLon);
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => DirectionsScreen(
                            initialDestinationSearchAddress: widget.placeDetail['place_name'],
                            startLat: startLat,
                            startLon: startLon,
                            endLat: endLat,
                            endLon: endLon,
                          ),
                        ),
                      );
                    },
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
              Center(
                child: Stack(
                  children: [
                    KakaoMapView(
                      width: MediaQuery.of(context).size.width * 0.8,
                      height: MediaQuery.of(context).size.width * 0.8,
                      kakaoMapKey: appKey!,
                      lat:
                          double.tryParse(widget.placeDetail['y'].toString()) ??
                              0.0,
                      lng:
                          double.tryParse(widget.placeDetail['x'].toString()) ??
                              0.0,
                      showZoomControl: false,
                      showMapTypeControl: false,
                      customScript: customScript,
                    ),
                  ],
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
              SizedBox(height: 40.0),
              Text(
                '배리어프리 시설',
                style: TextStyle(
                  fontSize: 20.0,
                  fontWeight: FontWeight.bold,
                  color: mainBlack,
                ),
              ),
              SizedBox(height: 10.0),
              Row(
                children: [
                  FutureBuilder<Map<String, dynamic>>(
                    future: barrierFreeDetailsFuture,
                    builder: (context, snapshot) {
                      if (snapshot.connectionState == ConnectionState.waiting) {
                        return Center(child: CircularProgressIndicator());
                      } else if (snapshot.hasError) {
                        return Text('장애물 없는 시설 정보를 가져오는 데 실패했습니다.');
                      } else if (snapshot.hasData && snapshot.data != null) {
                        var barrierFreeDetailList =
                            snapshot.data!['barrierFree'];
                        if (barrierFreeDetailList != null &&
                            barrierFreeDetailList.isNotEmpty) {
                          var barrierFreeList =
                              List<String>.from(barrierFreeDetailList as List);

                          return SingleChildScrollView(
                            scrollDirection: Axis.horizontal,
                            child: Row(
                              children: _buildBarrierFreeButtons(
                                  barrierFreeList), // 캐스팅된 리스트를 전달
                            ),
                          );
                        } else {
                          return Text('배리어프리 시설 정보가 없습니다');
                        }
                      } else {
                        return Text('배리어프리 시설 정보가 없습니다.');
                      }
                    },
                  ),
                  // SizedBox(width: 10.0),
                ],
              ),
              SizedBox(height: 40.0),
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
                ],
              ),
              SizedBox(height: 20.0),
              FutureBuilder<List<dynamic>>(
                  future: reviewListFuture,
                  builder: (context, snapshot) {
                    if (snapshot.connectionState == ConnectionState.waiting) {
                      return Center(child: CircularProgressIndicator());
                    } else if (snapshot.hasError) {
                      return Text('리뷰를 불러오는데 실패했습니다: ${snapshot.error}');
                    } else if (snapshot.hasData && snapshot.data!.isNotEmpty) {
                      return ListView.builder(
                        physics: NeverScrollableScrollPhysics(),
                        shrinkWrap: true,
                        itemCount: snapshot.data!.length,
                        itemBuilder: (context, index) {
                          var review = snapshot.data![index];
                          List<Widget> likeButtons =
                              review['lik'].map<Widget>((item) {
                            return ElevatedButton.icon(
                              icon: Icon(Icons.thumb_up_outlined,
                                  color: mainOrange),
                              label: Text(item),
                              onPressed: () {}, // 더미 함수, 필요한 기능으로 채울 것
                              style: ElevatedButton.styleFrom(
                                  side: BorderSide(
                                    color: mainOrange,
                                  ),
                                  backgroundColor: Colors.white,
                                  foregroundColor: mainOrange),
                            );
                          }).toList();

                          List<Widget> unlikeButtons =
                              review['unlik'].map<Widget>((item) {
                            return ElevatedButton.icon(
                              icon: Icon(Icons.thumb_down_outlined,
                                  color: mainOrange),
                              label: Text(item),
                              onPressed: () {}, // 더미 함수, 필요한 기능으로 채울 것
                              style: ElevatedButton.styleFrom(
                                  side: BorderSide(
                                    color: mainOrange,
                                  ),
                                  backgroundColor: Colors.white,
                                  foregroundColor: mainOrange),
                            );
                          }).toList();

                          return ListTile(
                            leading: review['img'] != null &&
                                    review['img'].isNotEmpty
                                ? Image.network(
                                    review['img'][0],
                                    width: 130,
                                    height: 130,
                                  )
                                : null,
                            title: Text(review['nickname']),
                            subtitle: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(review['content']),
                                Wrap(
                                  spacing: 8.0,
                                  children: likeButtons + unlikeButtons,
                                ),
                              ],
                            ),
                          );
                        },
                      );
                    } else {
                      return Text('리뷰가 없습니다.');
                    }
                  }),

              SizedBox(height: 40.0), // 간격

              ElevatedButton(
                onPressed: () async {
                  // 배리어프리 시설 상세 정보를 기다립니다.
                  List<String> barrierFreeItemsToSend =
                      barrierFreeDetails ?? [];

                  // ReviewScreen으로 이동합니다.
                  final result = await Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => ReviewScreen(
                        poiId: widget.placeDetail['id'],
                        barrierFreeItems: barrierFreeItemsToSend,
                      ),
                    ),
                  );
                  // 필요하다면 리뷰 목록을 새로고침 합니다.
                  if (result == true) {
                    refreshReviews();
                  }
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
                      '리뷰 작성하기',
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
