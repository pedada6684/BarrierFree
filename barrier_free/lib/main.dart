import 'package:barrier_free/component/bottomBar.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/provider/user_provider.dart';
import 'package:barrier_free/screen/directions/directions_screen.dart';
import 'package:barrier_free/screen/mypage/mypage_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:provider/provider.dart';
import 'package:url_launcher/url_launcher.dart';

import 'screen/map/map_screen.dart';
import 'services/location_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await dotenv.load(fileName: ".env");
  dotenv.load();

  await LocationService().getCurrentPosition();
  runApp(
    ChangeNotifierProvider(
      create: (context) => UserProvider(),
      child: MyApp(),
    ),
  );
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  //지도(MapScreen)가 먼저 뜨도록 index 1로 설정
  int _selectedIndex = 0;

  // 화면 리스트
  final List<Widget> _screens = [
    MapScreen(), //지도
    DirectionsScreen(),
    MyPageScreen(),
  ];

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    Provider.of<UserProvider>(context, listen: false)
        .addListener(_onUserProviderChange);
  }

  @override
  void dispose() {
    // TODO: implement dispose
    Provider.of<UserProvider>(context, listen: false)
        .removeListener(_onUserProviderChange);
    super.dispose();
  }

  void _onUserProviderChange() {
    //로그인 따라서 _selectedIndex 업데이트 시키기
    setState(() {
      if (Provider.of<UserProvider>(context, listen: false).isLoggedIn()) {
        setState(() {
          _selectedIndex = 0;
        });
      } else {
        setState(() {
          _selectedIndex = 0;
        });
      }
    });
  }

  void _onItemTapped(int index) {
    if (index == 2) {
      _makePhoneCall('15881668');
    } else {
      if (index < 2) {
        setState(() {
          _selectedIndex = index;
        });
      } else if (index > 2) {
        setState(() {
          _selectedIndex = 2;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        pageTransitionsTheme: PageTransitionsTheme(builders: {
          TargetPlatform.android: CupertinoPageTransitionsBuilder(),
        }),
        appBarTheme: AppBarTheme(
            centerTitle: true,
            iconTheme: IconThemeData(
              color: mainOrange,
            ),
            titleTextStyle: TextStyle(
                color: mainOrange,
                fontWeight: FontWeight.bold,
                fontSize: 22.0)),
      ),
      home: Scaffold(
        body: Center(
          child: _screens.elementAt(_selectedIndex), // 현재 선택된 화면을 표시
        ),
        bottomNavigationBar: CustomBottomNavigationBar(
          selectedIndex: _selectedIndex,
          onItemSelected: _onItemTapped,
        ),
      ),
    );
  }
}

void _makePhoneCall(String phoneNumber) async {
  final Uri launchUri = Uri(
    scheme: 'tel',
    path: phoneNumber,
  );
  if (await canLaunchUrl(launchUri)) {
    await launchUrl(launchUri);
  } else {
    throw '$launchUri를 설치하지 못했습니다.';
  }
}
