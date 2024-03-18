import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/services/location_service.dart';
import 'package:flutter/material.dart';

class DirectionsScreen extends StatefulWidget {
  const DirectionsScreen({super.key});

  @override
  State<DirectionsScreen> createState() => _DirectionsScreenState();
}

class _DirectionsScreenState extends State<DirectionsScreen> {
  final TextEditingController _originController = TextEditingController();
  final TextEditingController _destinationController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: '길찾기'),
      body: Column(
        children: [
          Container(
            color: mainOrange,
            height: MediaQuery.of(context).size.height * 0.25,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  TextField(
                    controller: _originController,
                    decoration: InputDecoration(
                      contentPadding:
                          const EdgeInsets.symmetric(horizontal: 8.0),
                      hintText: '출발지를 입력하세요.',
                      filled: true,
                      fillColor: Colors.white,
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(8.0),
                        borderSide: BorderSide.none,
                      ),
                    ),
                  ),
                  SizedBox(
                    height: 8.0,
                  ),
                  TextField(
                    controller: _destinationController,
                    decoration: InputDecoration(
                      contentPadding: EdgeInsets.symmetric(horizontal: 8.0),
                      hintText: '도착지를 입력하세요.',
                      filled: true,
                      fillColor: Colors.white,
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(8.0),
                        borderSide: BorderSide.none,
                      ),
                    ),
                  ),
                  IconButton(
                    onPressed: () async {
                      if (_originController.text.isNotEmpty) {
                        try {
                          final locationService = LocationService();
                          final originPlace = await locationService
                              .getPlace(_originController.text);
                          final originLat =
                              await originPlace['geometry']['location']['lat'];
                          final originLng =
                              await originPlace['geometry']['location']['lng'];

                          final destinationPlace = await locationService
                              .getPlace(_destinationController.text);
                          final destinationLat =
                              destinationPlace['geometry']['location']['lat'];
                          final destinationLng =
                              destinationPlace['geometry']['location']['lng'];

                          print('===================출발지 위도: $originLat, 출발지 경도: $originLng===================');
                          print('===================도착지 위도: $destinationLat, 도착지 경도: $destinationLng===================');


                        } catch (e) {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(
                              content: Text('경로를 찾을 수 없습니다.'),
                            ),
                          );
                        }
                      }
                    },
                    icon: Icon(
                      Icons.search,
                      color: Colors.white,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
