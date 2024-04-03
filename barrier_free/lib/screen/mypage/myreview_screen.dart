import 'dart:ui';

import 'package:barrier_free/const/color.dart';
import 'package:flutter/material.dart';
import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/services/review_service.dart';
import 'package:provider/provider.dart';
import 'package:barrier_free/provider/user_provider.dart';

class MyReviewScreen extends StatefulWidget {
  const MyReviewScreen({Key? key}) : super(key: key);

  @override
  _MyReviewScreenState createState() => _MyReviewScreenState();
}

class _MyReviewScreenState extends State<MyReviewScreen> {
  late Future<List<dynamic>> reviewListFuture;

  @override
  void initState() {
    super.initState();
    final userId = Provider.of<UserProvider>(context, listen: false).userId;
    if (userId != null) {
      reviewListFuture = ReviewService().fetchReviewByMemberid(userId);
    } else {
      const Center(child: Text('로그인이 필요한 화면입니다.'));
    }
  }

  @override
  Widget build(BuildContext context) {
    final userProvider = Provider.of<UserProvider>(context);

    final String? profileImageUrl = userProvider.profileImage;

    ImageProvider imageProvider;

    if (profileImageUrl != null && profileImageUrl.isNotEmpty) {
      imageProvider = NetworkImage(profileImageUrl);
    } else {
      imageProvider = AssetImage('assets/default_profile.png');
    }

    return Scaffold(
      appBar: CustomAppBar(title: '게시글'),
      body: FutureBuilder<List<dynamic>>(
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
                List<Widget> likeButtons = review['lik'].map<Widget>((item) {
                  return ElevatedButton.icon(
                    icon: Icon(Icons.thumb_up_outlined, color: mainOrange),
                    label: Text(item),
                    onPressed: () {}, // 더미 함수, 필요한 기능으로 채울 것
                    style: ElevatedButton.styleFrom(
                        side: BorderSide(
                          color: mainOrange,
                        ),
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(10.0)),
                        backgroundColor: Colors.white,
                        surfaceTintColor: Colors.white,
                        foregroundColor: mainOrange),
                  );
                }).toList();

                List<Widget> unlikeButtons =
                    review['unlik'].map<Widget>((item) {
                  return ElevatedButton.icon(
                    icon: Icon(Icons.thumb_down_outlined, color: mainOrange),
                    label: Text(item),
                    onPressed: () {}, // 더미 함수, 필요한 기능으로 채울 것
                    style: ElevatedButton.styleFrom(
                        side: BorderSide(
                          color: mainOrange,
                        ),
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(10.0)),
                        backgroundColor: Colors.white,
                        surfaceTintColor: Colors.white,
                        foregroundColor: mainOrange),
                  );
                }).toList();

                return Padding(
                  padding: const EdgeInsets.all(20.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Padding(
                            padding: const EdgeInsets.only(right: 16.0),
                            child: CircleAvatar(
                              backgroundImage: imageProvider,
                            ),
                          ),
                          Text(
                            review['nickname'],
                            style: TextStyle(
                              fontSize: 16.0,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                      SizedBox(height: 16.0),
                      Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          if (review['img'] != null && review['img'].isNotEmpty)
                            Image.network(
                              review['img'][0],
                              width: 120,
                              height: 120,
                              fit: BoxFit.cover,
                            ),
                          SizedBox(width: 10),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                // 버튼들을 가로로 배열
                                SingleChildScrollView(
                                  scrollDirection: Axis.horizontal,
                                  child: Row(
                                    children: [
                                      ...likeButtons.map((button) => Padding(
                                            padding: EdgeInsets.symmetric(
                                                horizontal: 4.0),
                                            // 원하는 간격으로 조정
                                            child: button,
                                          )),
                                      ...unlikeButtons.map((button) => Padding(
                                            padding: EdgeInsets.symmetric(
                                                horizontal: 4.0),
                                            // 원하는 간격으로 조정
                                            child: button,
                                          )),
                                    ],
                                  ),
                                ),
                                SizedBox(height: 8.0),
                                Padding(
                                  padding: const EdgeInsets.only(left: 8.0),
                                  child: Text(review['content']),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                );
              },
            );
          } else {
            return Center(
                child: Text(
              '리뷰가 없습니다.',
              style: TextStyle(fontSize: 16.0),
            ));
          }
        },
      ),
    );
  }
}
