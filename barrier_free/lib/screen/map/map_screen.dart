import 'package:barrier_free/component/appBar.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  static const platform = MethodChannel('com.example.barrier_free/tmap');

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(
        title: '베프',
        titleStyle: TextStyle(
          fontFamily: 'LogoFont',
          fontSize: 32.0,
        ),
      ),
      body: AndroidView(
        viewType: 'showTMap',
        creationParams: {},
        creationParamsCodec: StandardMessageCodec(),
      ),
    );
  }

  Future<void> _showTMapView() async {
    try {
      final String result = await platform.invokeMethod('showTMap');

      print('결과 : $result');
    } on PlatformException catch (e) {
      print("지도 불러오기 실패! '${e.message}'");
    }
  }
}
