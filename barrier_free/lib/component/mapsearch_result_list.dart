import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/screen/place/placedetail_screen.dart';
import 'package:flutter/material.dart';

class MapSearchResultList extends StatelessWidget {
  final List<dynamic> searchResults;
  final List<dynamic> startsearchResults;
  final List<dynamic> destinationsearchResults;

  const MapSearchResultList({
    super.key,
    required this.searchResults,
    required this.startsearchResults,
    required this.destinationsearchResults,
  });

  @override
  Widget build(BuildContext context) {
    //distance 있을때 만 필터링
    List<dynamic> filteredResults = searchResults
        .where((result) =>
            result.containsKey('distance') && result['distance'] != null)
        .toList();

    return ListView(
      children: [
        const Padding(
          padding: EdgeInsets.symmetric(horizontal: 16.0, vertical: 20.0),
          child: Text(
            '검색 결과',
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 22.0,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        const SizedBox(height: 8.0),
        ...filteredResults.map((result) {
          double distanceInKm = int.parse(result['distance']) / 1000;
          String categoryName = result['category_name'];
          List<String> categoryDetail = categoryName.split('>');
          String categoryReal = categoryDetail.length > 1
              ? categoryDetail[1].trim()
              : categoryName;
          return Padding(
            padding: const EdgeInsets.all(8.0),
            child: Container(
              child: ListTile(
                title: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      result['place_name'],
                      style: const TextStyle(
                        fontSize: 18.0,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    Text('${distanceInKm.toStringAsFixed(1)}km'),
                  ],
                ),
                subtitle: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    SizedBox(
                      height: 8.0,
                    ),
                    Text(
                      categoryReal,
                      style: TextStyle(color: mainGray),
                    ),
                    Row(
                      children: [
                        Text(
                          result['road_address_name'] ?? '주소 정보 없음',
                          style: TextStyle(color: mainGray),
                        ),
                      ],
                    ),
                    Text(
                      '${result['phone']}',
                      style: TextStyle(color: mainGray),
                    ),
                  ],
                ),
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => PlaceDetailScreen(
                        placeDetail: result,
                        placeCategory: categoryReal,
                        isStart: true,
                      ),
                    ),
                  );
                },
              ),
            ),
          );
        }).toList(),
        const SizedBox(height: 8.0),
        ...startsearchResults.map((result) {
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
                // Container(
                //   height: 1, // 수평선의 높이
                //   color: Colors.grey, // 수평선의 색상
                //   margin: EdgeInsets.symmetric(vertical: 10), // 수평선의 위아래 여백 조절
                // ),
              ],
            ),
            onTap: () {
              // 출발지 상세 화면으로 이동
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => PlaceDetailScreen(
                    placeDetail: result,
                    placeCategory: categoryReal,
                    isStart: true,
                  ),
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
                  builder: (context) => PlaceDetailScreen(
                    placeDetail: result,
                    placeCategory: categoryReal,
                    isStart: false,
                  ),
                ),
              );
            },
          );
        }).toList(),
      ],
    );
  }
}
