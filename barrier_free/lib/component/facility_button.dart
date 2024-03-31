import 'package:barrier_free/const/color.dart';
import 'package:flutter/material.dart';

class CustomFacilityButton extends StatefulWidget {
  final Function(String) onFeatureSelected;

  const CustomFacilityButton({super.key, required this.onFeatureSelected});

  @override
  State<CustomFacilityButton> createState() => _CustomFacilityButtonState();
}

class _CustomFacilityButtonState extends State<CustomFacilityButton> {
  String selectedCategory = '';

  Widget _buildFacilityButton(BuildContext context, String label) {
    bool isSelected = label == selectedCategory;
    return GestureDetector(
      onTap: () {
        setState(() {
          selectedCategory = label;
        });
        widget.onFeatureSelected(label);
      },
      child: Container(
        padding: EdgeInsets.symmetric(horizontal: 10.0),
        margin: EdgeInsets.symmetric(horizontal: 4.0),
        decoration: BoxDecoration(
          color: isSelected ? mainOrange : Colors.white,
          border: Border.all(
            color: mainOrange,
            width: 1.5,
          ),
          borderRadius: BorderRadius.circular(8.0),
        ),
        child: Center(
          child: Text(
            label,
            textAlign: TextAlign.center,
            style: TextStyle(
              color: isSelected ? Colors.white : mainBlack,
              fontSize: 18.0,
            ),
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.all(8.0),
      color: Colors.transparent,
      height: 52.0,
      child: ListView(
        scrollDirection: Axis.horizontal,
        children: <Widget>[
          //휠체어 충전소, 화장실, 음식점, 병원, 문화, 편의, 숙박
          _buildFacilityButton(context, '즐겨찾기'),
          _buildFacilityButton(context, '휠체어 충전소'),
          _buildFacilityButton(context, '화장실'),
          _buildFacilityButton(context, '음식점'),
          _buildFacilityButton(context, '병원'),
          _buildFacilityButton(context, '문화'),
          _buildFacilityButton(context, '편의'),
          _buildFacilityButton(context, '숙박'),
        ],
      ),
    );
  }
}

// Widget _buildFacilityButton(
//     BuildContext context, String label, Function(String) onSelected) {
//   return GestureDetector(
//     onTap: () => onSelected(label),
//     child: Container(
//       padding: EdgeInsets.symmetric(horizontal: 10.0),
//       margin: EdgeInsets.symmetric(horizontal: 4.0),
//       decoration: BoxDecoration(
//         color: Colors.white,
//         border: Border.all(
//           color: mainOrange,
//           width: 1.5,
//         ),
//         borderRadius: BorderRadius.circular(8.0),
//       ),
//       child: Center(
//         child: Text(
//           label,
//           textAlign: TextAlign.center,
//           style: TextStyle(
//             color: mainBlack,
//             fontSize: 16.0,
//           ),
//         ),
//       ),
//     ),
//   );
// }
