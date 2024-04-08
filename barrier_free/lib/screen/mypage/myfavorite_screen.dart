import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/provider/location_provider.dart';
import 'package:barrier_free/provider/user_provider.dart';
import 'package:barrier_free/screen/place/placedetail_screen.dart';
import 'package:barrier_free/services/bookmarkPlace_service.dart';
import 'package:barrier_free/services/search_service.dart';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:provider/provider.dart';

class MyFavoriteScreen extends StatefulWidget {
  const MyFavoriteScreen({super.key});

  @override
  State<MyFavoriteScreen> createState() => _MyFavoriteScreenState();
}

class _MyFavoriteScreenState extends State<MyFavoriteScreen> {
  Future<List<dynamic>>? myBmList;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadMyBookMarkList();
    });
  }

  Future<void> _loadMyBookMarkList() async {
    try {
      final userProvider = Provider.of<UserProvider>(context, listen: false);
      final userId = userProvider.userId;

      if (userId != null) {
        final bookmarks =
            await bookmarkPlaceService().fetchBookMarkByUserId(userId);
        setState(() {
          myBmList = Future.value(bookmarks);
        });
        print(bookmarks);
      }
    } catch (e) {
      print('북마크 리스트 불러오는 동안 오류 발생!!!');
    }
  }

  void _onBookmarkTap(dynamic bookmark) async {
    //poi로 카카오 호출
    final keyword = bookmark['placeName'];

    try {
      Position currentPosition = await Geolocator.getCurrentPosition(
          desiredAccuracy: LocationAccuracy.high);

      final searchResults = await fetchSearchResults(keyword, currentPosition);
      if (searchResults.isNotEmpty) {
        Navigator.push(
            context,
            MaterialPageRoute(
                builder: (context) => PlaceDetailScreen(
                    placeDetail: searchResults.first,
                    placeCategory: bookmark['category'],
                    isStart: false)));
      } else {
        throw Exception('searchResults null');
      }
    } catch (e) {
      print('즐겨찾기 검색 실패 $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(title: '즐겨찾기'),
      body: myBmList == null
          ? Center(
              child: CircularProgressIndicator(),
            )
          : FutureBuilder<List<dynamic>>(
              future: myBmList,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return Center(child: CircularProgressIndicator());
                } else if (snapshot.hasError) {
                  return Center(child: Text('데이터 로드 중 오류가 발생했습니다.'));
                } else if (snapshot.hasData) {
                  final bookmarks = snapshot.data!;
                  return ListView.builder(
                    itemCount: bookmarks.length,
                    itemBuilder: (context, index) {
                      final bookmark = bookmarks[index];
                      return Column(
                        children: [
                          Padding(
                            padding: const EdgeInsets.symmetric(
                                vertical: 10.0, horizontal: 10.0),
                            child: ListTile(
                              title: Text(
                                bookmark['placeName'],
                                style: const TextStyle(fontSize: 20.0),
                              ),
                              subtitle: Text(
                                bookmark['address'],
                                style: const TextStyle(
                                    fontSize: 16.0, color: Colors.grey),
                              ),
                              trailing: const Icon(Icons.arrow_forward_ios,
                                  color: mainGray),
                              onTap: () {
                                _onBookmarkTap(bookmark);
                              },
                            ),
                          ),
                          Divider(
                              height: 1,
                              color: Colors.grey.withOpacity(0.5),
                              indent: 20,
                              endIndent: 20),
                        ],
                      );
                    },
                  );
                } else {
                  return Center(child: Text('데이터가 없습니다.'));
                }
              },
            ),
    );
  }
}
