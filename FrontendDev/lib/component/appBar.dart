import 'package:flutter/material.dart';

class CustomAppBar extends StatelessWidget implements PreferredSizeWidget {
  final String title;
  final List<Widget>? actions;

  const CustomAppBar({
    super.key,
    required this.title,
    this.actions,
  });

  @override
  Widget build(BuildContext context) {
    return AppBar(
      centerTitle: true,
      title: Text(title),
      actions: actions,
      backgroundColor: Colors.white,
    );
  }

  @override
  //사이즈 지정
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);
}
