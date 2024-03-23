import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/const/color.dart';
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
                    onPressed: (){},
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
