import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/provider/location_provider.dart';
import 'package:barrier_free/provider/user_provider.dart';
import 'package:barrier_free/screen/directions/directions_screen.dart';
import 'package:barrier_free/screen/mypage/mypage_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:persistent_bottom_nav_bar/persistent_tab_view.dart';
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
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (context) => UserProvider()),
        ChangeNotifierProvider(create: (context) => LocationProvider()),
        // LocationProvider 추가
      ],
      child: MyApp(),
    ),
  );
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  GlobalKey mapScreenKey = GlobalKey();
  late PersistentTabController _controller;

  // 화면 리스트
  List<Widget> _buildScreens() {
    return [
      MapScreen(
        key: mapScreenKey,
      ), //지도
      DirectionsScreen(),
      Container(), //콜택시용 더미 화면
      MyPageScreen(),
    ];
  }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _controller = PersistentTabController(initialIndex: 0);
  }

  void resetContoller() {
    setState(() {
      _controller = PersistentTabController(initialIndex: 0);
    });
  }

  List<PersistentBottomNavBarItem> _navBarsItems() {
    return [
      PersistentBottomNavBarItem(
        icon: Icon(Icons.map, color: Colors.white),
        title: ("지도"),
        activeColorPrimary: Colors.white,
        inactiveColorPrimary: Colors.white,
      ),
      PersistentBottomNavBarItem(
        icon: Icon(Icons.directions, color: Colors.white),
        title: ("길찾기"),
        activeColorPrimary: Colors.white,
        inactiveColorPrimary: Colors.white,
      ),
      PersistentBottomNavBarItem(
        icon: Icon(Icons.local_taxi, color: Colors.white),
        title: ("콜택시"),
        activeColorPrimary: Colors.white,
        inactiveColorPrimary: Colors.white,
        onPressed: (context) => _makePhoneCall('15881668'), // 수정된 부분
      ),
      PersistentBottomNavBarItem(
        icon: Icon(Icons.person, color: Colors.white),
        title: ("마이"),
        activeColorPrimary: Colors.white,
        inactiveColorPrimary: Colors.white,
      ),
    ];
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

  // void _onItemTapped(int index) {
  //   if (index == 2) {
  //     _makePhoneCall('15881668');
  //   } else {
  //     if (index < 2) {
  //       setState(() {
  //         _selectedIndex = index;
  //       });
  //     } else if (index > 2) {
  //       setState(() {
  //         _selectedIndex = 2;
  //       });
  //     }
  //   }
  // }

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
      home: PersistentTabView(
        context,
        controller: _controller,
        screens: _buildScreens(),
        items: _navBarsItems(),
        confineInSafeArea: true,
        backgroundColor: mainOrange,
        navBarStyle: NavBarStyle.style6,
        onItemSelected: (int index) {
          if (index == 0) {
            // MapScreen이 첫 번째 탭이라고 가정
            Provider.of<LocationProvider>(context, listen: false)
                .updateLocation();
          }
        },
        // onItemSelected: (int index) {
        //   if (index == 3) {
        //     //마이페이지 탭
        //     _controller.jumpToTab(0);
        //   }
        // },
      ),
    );
  }
}
