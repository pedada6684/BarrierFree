import 'package:barrier_free/const/color.dart';
import 'package:flutter/material.dart';

class CustomBottomNavigationBar extends StatelessWidget {
  final int selectedIndex;
  final Function(int) onItemSelected;

  const CustomBottomNavigationBar({
    super.key,
    required this.selectedIndex,
    required this.onItemSelected,
  });

  @override
  Widget build(BuildContext context) {
    return BottomNavigationBar(
      type: BottomNavigationBarType.fixed,
      backgroundColor: mainOrange,
      items: const <BottomNavigationBarItem>[
        BottomNavigationBarItem(
          icon: Icon(Icons.place_outlined, color: Colors.white),
          label: '지도',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.directions_bus_outlined, color: Colors.white),
          label: '길찾기',
        ),
        BottomNavigationBarItem(
            icon: Icon(Icons.local_taxi_outlined, color: Colors.white),
            label: '콜택시'),
        BottomNavigationBarItem(
          icon: Icon(Icons.person_outlined, color: Colors.white),
          label: '마이',
        ),
      ],
      currentIndex: selectedIndex,
      selectedItemColor: Colors.white,
      unselectedItemColor: Colors.white,
      onTap: onItemSelected,
    );
  }
}
