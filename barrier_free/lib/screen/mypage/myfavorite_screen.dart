import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/screen/place/placedetail_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:barrier_free/const/color.dart';

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

class MyFavoriteScreen extends StatefulWidget {
  const MyFavoriteScreen({super.key});

  @override
  State<MyFavoriteScreen> createState() => _MyFavoriteScreenState();
}

class _MyFavoriteScreenState extends State<MyFavoriteScreen> {
  final List<PlaceItem> listItems = [
    PlaceItem(
      title: '삼성화재 유성연수원',
      address: '대전광역시 유성구',
      builder: (context) => PlaceDetailScreen(
        placeDetail: {},
        placeCategory: '',
      ),
    ),
    PlaceItem(
      title: '하이테이블',
      address: '대전광역시 유성구',
      builder: (context) => PlaceDetailScreen(
        placeDetail: {},
        placeCategory: '',
      ),
    ),
    // 추가 항목들을 이곳에 추가할 수 있습니다.
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(title: '즐겨찾기'),
      body: ListView(
        children: [
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
                  padding: const EdgeInsets.symmetric(
                      vertical: 10.0, horizontal: 10.0), // 수직 10, 수평 20
                  child: ListTile(
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
                    trailing: const Icon(
                      Icons.arrow_forward_ios,
                      color: mainGray,
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
}
