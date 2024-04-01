import 'package:barrier_free/screen/place/placedetail_screen.dart';
import 'package:flutter/material.dart';

class DirectionSearchResultList extends StatelessWidget {
  final List<dynamic> searchResults;
  final List<dynamic> startsearchResults;
  final List<dynamic> destinationsearchResults;


  const DirectionSearchResultList({
    super.key,
    required this.searchResults,
    required this.startsearchResults,
    required this.destinationsearchResults,
  });

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        // const Padding(
        //   padding: EdgeInsets.all(16.0),
        //   child: Text(
        //     '검색 결과',
        //     textAlign: TextAlign.center,
        //     style: TextStyle(
        //       fontSize: 18.0,
        //       fontWeight: FontWeight.bold,
        //     ),
        //   ),
        // ),
        const SizedBox(height: 8.0),
        ...searchResults.map((result) {
          // print('searchResults=========================');
          // print(result);
          String categoryName = result['category_name'];
          List<String> categoryDetail = categoryName.split('>');
          String categoryReal = categoryDetail.length > 1
              ? categoryDetail[1].trim()
              : categoryName;
          return ListTile(
            title: Text(
              result['place_name'],
              style: const TextStyle(
                fontSize: 20.0,
                fontWeight: FontWeight.bold,
              ),
            ),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                Text(categoryReal),
                Text(result['road_address_name'] ?? '주소 정보 없음'),
                Text('${result['phone']}'),
              ],
            ),
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => PlaceDetailScreen(placeDetail: result, placeCategory: categoryReal, isStart: true,),
                ),
              );
            },
          );
        }).toList(),
        const SizedBox(height: 8.0),
        ...startsearchResults.map((result) {
          // print('startSearchResults=========================');
          // print(result);
          String categoryName = result['category_name'];
          List<String> categoryDetail = categoryName.split('>');
          String categoryReal = categoryDetail.length > 1
              ? categoryDetail[1].trim()
              : categoryName;

          // 출발지 검색 결과인 경우
          return ListTile(
            title: Text(
              result['place_name'],
              style: TextStyle(
                fontSize: 20.0,
                fontWeight: FontWeight.bold,
              ),
            ),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                // Text('출발지'),
                Text(categoryReal),
                Text(result['road_address_name'] ?? '주소 정보 없음'),
                Text('${result['phone']}'),
              ],
            ),
            onTap: () {
              // 출발지 상세 화면으로 이동
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => PlaceDetailScreen(placeDetail: result, placeCategory: categoryReal, isStart: true,),
                ),
              );
            },
          );
        }).toList(),
        const SizedBox(height: 8.0),
        ...destinationsearchResults.map((result) {
          // print('endSearchResults=========================');
          // print(result);
          String categoryName = result['category_name'];
          List<String> categoryDetail = categoryName.split('>');
          String categoryReal = categoryDetail.length > 1
              ? categoryDetail[1].trim()
              : categoryName;

          // 도착지 검색 결과인 경우
          return ListTile(
            title: Text(
              result['place_name'],
              style: TextStyle(
                fontSize: 20.0,
                fontWeight: FontWeight.bold,
              ),
            ),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                // Text('도착지'),
                Text(categoryReal),
                Text(result['road_address_name'] ?? '주소 정보 없음'),
                Text('${result['phone']}'),
              ],
            ),
            onTap: () {
              // 도착지 상세 화면으로 이동
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => PlaceDetailScreen(placeDetail: result, placeCategory: categoryReal, isStart: false,),
                ),
              );
            },
          );
        }).toList(),
      ],
    );
  }
}
