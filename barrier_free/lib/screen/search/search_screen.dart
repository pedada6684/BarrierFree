import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/screen/place/placedetail_screen.dart';
import 'package:flutter/material.dart';

class PlaceItem {
  final String title;
  final String address;
  final Widget Function(BuildContext) builder;

  PlaceItem({
    required this.title,
    required this.address,
    required this.builder,
  });
}

class SearchScreen extends StatefulWidget {
  const SearchScreen({super.key});

  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> {
  final List<PlaceItem> listItems = [
    PlaceItem(
      title: '삼성화재 유성연수원',
      address: '대전광역시 유성구',
      builder: (context) => PlaceDetailScreen(),
    ),
    PlaceItem(
      title: '하이테이블',
      address: '대전광역시 유성구',
      builder: (context) => PlaceDetailScreen(),
    ),
  ];

  // 위치 정보를 입력받는 텍스트 필드 컨트롤러
  late TextEditingController _originController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(title: '검색'),
      body: Column(
        children: [
          // 검색 바
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 10.0),
            child: Container(
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
                      // 검색어 입력 필드
                      decoration: InputDecoration(
                        contentPadding: EdgeInsets.symmetric(horizontal: 20.0),
                        hintText: '검색어를 입력해주세요.',
                        hintStyle: TextStyle(
                            color: mainGray, fontSize: 18.0),
                        border: OutlineInputBorder(borderSide: BorderSide.none),
                      ),
                      textCapitalization: TextCapitalization.words,
                      controller: _originController,
                      onChanged: (value) {
                        print(value);
                      },
                    ),
                  ),
                  Padding(
                    padding: EdgeInsets.symmetric(horizontal: 10.0), // 패딩 추가
                    child: InkWell(
                      onTap: () {
                        // 검색 버튼 클릭 시 액션
                        Navigator.push(
                          context,
                          MaterialPageRoute(builder: (context) => SearchScreen()),
                        );
                      },
                      child: Icon(
                        Icons.search,
                        color: mainOrange,
                        size: 40.0,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          SizedBox(
            height: 16.0,
          ),
          ..._buildListItems(),
        ],
      ),
    );
  }

  List<Widget> _buildListItems() {
    return listItems
        .map((placeItem) => Column(
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 10.0, horizontal: 10.0), // 수직 10, 수평 20
            child: ListTile(
              leading: const Icon(
                Icons.near_me_outlined,
                color: mainOrange,
              ),
              title: Text(
                placeItem.title,
                style: const TextStyle(
                  fontSize: 20.0,
                ),
              ),
              subtitle: Text(
                placeItem.address, // 주소 표시
                style: const TextStyle(
                  fontSize: 16.0,
                  color: Colors.grey, // 다른 스타일 적용 가능
                ),
              ),
              onTap: () {
                Navigator.push(context,
                    MaterialPageRoute(builder: placeItem.builder));
              },
            ),
          ),
        Divider(
          height: 1, // Divider 높이 조절
          color: Colors.grey.withOpacity(0.5), // Divider 색상
          indent: 20, // 시작 부분 여백
          endIndent: 20, // 끝 부분 여백
        ),
      ],
    ))
        .toList();
  }

  @override
  void dispose() {
    _originController.dispose();
    super.dispose();
  }
}


