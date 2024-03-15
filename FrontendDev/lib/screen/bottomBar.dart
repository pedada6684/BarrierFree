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
      backgroundColor: Color(0xfffca63d),
      items: const <BottomNavigationBarItem>[
        BottomNavigationBarItem(
          icon: Icon(Icons.home, color: Colors.white),
          label: '홈',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.map, color: Colors.white),
          label: '길찾기',
        ),
      ],
      currentIndex: selectedIndex,
      selectedItemColor: Colors.white,
      unselectedItemColor: Colors.white,
      onTap: onItemSelected,
    );
  }
}