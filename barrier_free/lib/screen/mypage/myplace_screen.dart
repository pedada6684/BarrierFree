import 'package:barrier_free/component/appBar.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:barrier_free/const/color.dart';

class PlaceItem {
  final IconData icon;
  final String title;
  final String address;
  final Widget Function(BuildContext) builder;

  PlaceItem({
    required this.icon,
    required this.title,
    required this.address,
    required this.builder,
  });
}

class MyPlaceScreen extends StatefulWidget {
  const MyPlaceScreen({super.key});

  @override
  State<MyPlaceScreen> createState() => _MyPlaceScreenState();
}

class _MyPlaceScreenState extends State<MyPlaceScreen> {
  final List<PlaceItem> listItems = [
    PlaceItem(
      icon: Icons.home,
      title: '집',
      address: '대전광역시 유성구',
      builder: (context) => MyPlaceScreen(),
    ),
    PlaceItem(
      icon: Icons.business,
      title: '회사',
      address: '장소를 설정해주세요.',
      builder: (context) => MyPlaceScreen(),
    ),
    // 추가 항목들을 이곳에 추가할 수 있습니다.
  ];


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(title: '내 장소'),
      body: ListView(
        children: [
          ..._buildListItems(),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          // 플로팅 액션 버튼을 눌렀을 때의 동작 구현
        },
        backgroundColor: mainOrange, // 배경색 설정
        foregroundColor: Colors.white, // 아이콘 색상 설정
        child: Icon(Icons.add), // 아이콘은 추가할 기능에 맞게 변경 가능
      ),
    );
  }

  List<Widget> _buildListItems() {
    return listItems.map((placeItem) => Column(
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(vertical: 10.0, horizontal: 10.0), // 수직 10, 수평 20
          child: ListTile(
            leading: Icon(placeItem.icon),
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

