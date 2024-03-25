import 'package:barrier_free/const/color.dart';
import 'package:flutter/material.dart';

class CustomFacilityButton extends StatelessWidget {
  final Function(String) onFeatureSelected;

  const CustomFacilityButton({super.key, required this.onFeatureSelected});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.all(8.0),
      color: Colors.transparent,
      height: 50.0,
      child: ListView(
        scrollDirection: Axis.horizontal,
        children: <Widget>[
          //휠체어 충전소, 화장실, 음식점, 병원, 문화, 편의, 숙박
          _buildFacilityButton(context, '즐겨찾기', onFeatureSelected),
          _buildFacilityButton(context, '휠체어 충전소', onFeatureSelected),
          _buildFacilityButton(context, '화장실', onFeatureSelected),
          _buildFacilityButton(context, '음식점', onFeatureSelected),
          _buildFacilityButton(context, '병원', onFeatureSelected),
          _buildFacilityButton(context, '문화', onFeatureSelected),
          _buildFacilityButton(context, '편의', onFeatureSelected),
          _buildFacilityButton(context, '숙박', onFeatureSelected),
        ],
      ),
    );
  }
}

Widget _buildFacilityButton(
    BuildContext context, String label, Function(String) onSelected) {
  return GestureDetector(
    onTap: () => onSelected(label),
    child: Container(
      padding: EdgeInsets.symmetric(horizontal: 10.0),
      margin: EdgeInsets.symmetric(horizontal: 4.0),
      decoration: BoxDecoration(
        color: Colors.white,
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
            color: mainBlack,
            fontSize: 16.0,
          ),
        ),
      ),
    ),
  );
}
