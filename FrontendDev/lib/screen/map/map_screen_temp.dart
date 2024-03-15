import 'dart:async';

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
      appBar: AppBar(
        centerTitle: true,
        title: const Text(
          '길찾기',
          style: TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.w700,
          ),
        ),
        backgroundColor: Color(0xfffca63d),
      ),
      body: Column(
        children: [
          Row(
            children: [
              Expanded(
                child: Column(
                  children: [
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 8.0),
                      child: TextFormField(
                        decoration: InputDecoration(
                          hintText: '출발지를 입력해주세요',
                        ),
                        textCapitalization: TextCapitalization.words,
                        controller: _originController,
                        onChanged: (value) {
                          print(value);
                        },
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 8.0),
                      child: TextFormField(
                        decoration: InputDecoration(
                          hintText: '도착지를 입력해주세요.',
                        ),
                        textCapitalization: TextCapitalization.words,
                        controller: _destinationController,
                        onChanged: (value) {
                          print(value);
                        },
                      ),
                    ),
                  ],
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
                icon: Icon(Icons.search),
              ),
            ],
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
