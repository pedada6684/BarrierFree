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

import 'package:barrier_free/providers/text_provider.dart';

final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();
late PersistentTabController globalPersistentTabController;
void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  globalPersistentTabController = PersistentTabController(initialIndex: 0);
  await dotenv.load(fileName: ".env");
  dotenv.load();

  await LocationService().getCurrentPosition();
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (context) => UserProvider()),
        ChangeNotifierProvider(create: (context) => LocationProvider()),
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
    globalPersistentTabController = PersistentTabController(initialIndex: 0);
  }

  void resetContoller() {
    setState(() {
      globalPersistentTabController = PersistentTabController(initialIndex: 0);
    });
  }

  List<PersistentBottomNavBarItem> _navBarsItems() {
    return [
      PersistentBottomNavBarItem(
        icon: Icon(Icons.pin_drop, color: Colors.white),
        inactiveIcon: Icon(Icons.pin_drop, color: Colors.white54),
        title: ("지도"),
        activeColorPrimary: Colors.white,
        inactiveColorPrimary: Colors.white54,
      ),
      PersistentBottomNavBarItem(
        icon: Icon(Icons.accessible_forward_outlined, color: Colors.white),
        inactiveIcon: Icon(Icons.accessible, color: Colors.white54),
        title: ("길찾기"),
        activeColorPrimary: Colors.white,
        inactiveColorPrimary: Colors.white54,
      ),
      PersistentBottomNavBarItem(
        icon: Icon(Icons.local_taxi, color: Colors.white),
        inactiveIcon: Icon(Icons.local_taxi, color: Colors.white54),
        title: ("장애인콜택시"),
        activeColorPrimary: Colors.white,
        inactiveColorPrimary: Colors.white54,
        onPressed: (context) => _makePhoneCall('15881668'), // 수정된 부분
      ),
      PersistentBottomNavBarItem(
        icon: Icon(Icons.account_circle, color: Colors.white),
        inactiveIcon: Icon(Icons.account_circle_outlined, color: Colors.white54),
        title: ("마이"),
        activeColorPrimary: Colors.white,
        inactiveColorPrimary: Colors.white54,
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

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      navigatorKey: navigatorKey,
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
            fontSize: 22.0,
          ),
        ),
      ),
      home: ChangeNotifierProvider(
        create: (context) => TextProvider(),
        child: PersistentTabView(
          context,
          controller: globalPersistentTabController,
          screens: _buildScreens(),
          items: _navBarsItems(),
          confineInSafeArea: true,
          backgroundColor: mainOrange,
          navBarStyle: NavBarStyle.style6,
          onItemSelected: (int index) {
            if (index == 0) {
              Provider.of<LocationProvider>(context, listen: false)
                  .updateLocation();
              mapScreenKey = GlobalKey();
            }
          },
        ),
      ),
    );
  }
}
