import 'dart:async';

import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/services/location_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_polyline_points/flutter_polyline_points.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  final CameraPosition _firstSpot = CameraPosition(
    target: LatLng(
      36.354946759143,
      127.29980994578,
    ),
    zoom: 18.0,
  );

  final CameraPosition _secondSpot = CameraPosition(
    target: LatLng(
      36.331139593396,
      127.43314242945,
    ),
    zoom: 18.0,
  );

  Completer<GoogleMapController> _controller = Completer();
  TextEditingController _originController = TextEditingController();
  TextEditingController _destinationController = TextEditingController();

  Set<Marker> _markers = Set<Marker>();
  Set<Polygon> _polygons = Set<Polygon>();
  Set<Polyline> _polylines = Set<Polyline>();
  List<LatLng> polygonLatLng = <LatLng>[];

  int _polygonIdCounter = 1;
  int _polylineIdCounter = 1;

  @override
  void initState() {
    super.initState();

    _setMarker(
      LatLng(
        36.354946759143,
        127.29980994578,
      ),
    );
  }

  void _setMarker(LatLng point) {
    final String markerIdVal = 'marker_${_markers.length}'; // 유니크한 ID 생성
    setState(
      () {
        _markers.add(
          Marker(
            markerId: MarkerId(markerIdVal),
            position: point,
            onTap: () {
              _showMarkerInfo(point.latitude, point.longitude); // 익명 함수 내에서 호출
            },
          ),
        );
      },
    );
  }

  void _setPolygon() {
    final String polygonIdVal = 'polygon_$_polygonIdCounter';
    _polygonIdCounter++;

    _polygons.add(
      Polygon(
        polygonId: PolygonId(polygonIdVal),
        points: polygonLatLng,
        strokeWidth: 2,
        fillColor: Colors.transparent,
      ),
    );
  }

  void _showMarkerInfo(double lat, double lng) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('위도: $lat, 경도: $lng'),
        duration: Duration(seconds: 3),
      ),
    );
  }

  void _setPolyline(List<PointLatLng> points) {
    final String polylineIdVal = 'polyline_$_polylineIdCounter';
    _polylineIdCounter++;

    List<LatLng> polylineCoordinates = points
        .map(
          (point) => LatLng(point.latitude, point.longitude),
        )
        .toList();

    _polylines.add(
      Polyline(
        polylineId: PolylineId(
          polylineIdVal,
        ),
        width: 2,
        color: Colors.blue,
        points: polylineCoordinates,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(title: '길찾기'),
      body: Column(
        children: [
          Container(
            width: 400.0,
            decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.all(Radius.circular(30.0)),
                border: Border.all(color: mainOrange, width: 2.5),
                boxShadow: [
                  BoxShadow(
                      color: Colors.grey.withOpacity(0.5),
                      spreadRadius: 2,
                      blurRadius: 4,
                      offset: Offset(3, 3))
                ]),
            child: Row(
              children: [
                Expanded(
                  child: TextFormField(
                    decoration: InputDecoration(
                      contentPadding: EdgeInsets.symmetric(horizontal: 16.0),
                      hintText: '검색어를 입력해주세요.',
                      hintStyle: TextStyle(
                          color: mainOrange,
                          fontWeight: FontWeight.w600),
                      border: OutlineInputBorder(borderSide: BorderSide.none),
                    ),
                    textCapitalization: TextCapitalization.words,
                    controller: _originController,
                    onChanged: (value) {
                      print(value);
                    },
                  ),
                ),
                IconButton(
                  onPressed: () async {
                    var directions = await LocationService().getDirections(
                        _originController.text, _destinationController.text);
                    if (directions != null) {
                      _goToStart(
                        directions['start_location']['lat'],
                        directions['start_location']['lng'],
                        directions['bounds_ne'],
                        directions['bounds_sw'],
                      );
                      _setPolyline(directions['polyline_decoded']);
                    }
                  },
                  icon: Icon(
                    Icons.search,
                    color: mainOrange,
                  ),
                ),
              ],
            ),
          ),
          Expanded(
            child: GoogleMap(
              mapType: MapType.normal,
              markers: _markers,
              polygons: _polygons,
              polylines: _polylines,
              myLocationEnabled: true,
              initialCameraPosition: _firstSpot,
              onMapCreated: (GoogleMapController controller) {
                _controller.complete(controller);
              },
              onTap: (point) {
                setState(() {
                  polygonLatLng.add(point);
                  _setPolygon();
                });
              },
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _goToStart(
    double lat,
    double lng,
    Map<String, dynamic> boundsNe,
    Map<String, dynamic> boundsSw,
  ) async {
    final GoogleMapController controller = await _controller.future;
    controller.animateCamera(CameraUpdate.newCameraPosition(
      CameraPosition(
        target: LatLng(lat, lng),
        zoom: 12,
      ),
    ));

    controller.animateCamera(
      CameraUpdate.newLatLngBounds(
          LatLngBounds(
              southwest: LatLng(
                boundsSw['lat'],
                boundsSw['lng'],
              ),
              northeast: LatLng(
                boundsNe['lat'],
                boundsNe['lng'],
              )),
          25),
    );

    _setMarker(
      LatLng(lat, lng),
    );
  }
}
