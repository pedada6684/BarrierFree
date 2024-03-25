import 'package:barrier_free/screen/place/placedetail_screen.dart';
import 'package:flutter/material.dart';

class MapSearchResultList extends StatelessWidget {
  final List<dynamic> searchResults;

  const MapSearchResultList({super.key, required this.searchResults});

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        const Padding(
          padding: EdgeInsets.all(16.0),
          child: Text(
            '검색 결과',
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 18.0,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        const SizedBox(height: 8.0),
        ...searchResults.map((result) {
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
                  builder: (context) => PlaceDetailScreen(placeDetail: result, placeCategory: categoryReal),
                ),
              );
            },
          );
        }).toList(),
      ],
    );
  }
}
