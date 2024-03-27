String generateMarkerScript(List<dynamic> searchResults) {
  String markerScript = "var markers = [];";

  for (var i = 0; i < searchResults.length; i++) {
    var result = searchResults[i];
    markerScript += """
      var position${i} = new kakao.maps.LatLng(${result['lat']}, ${result['lng']});
      var marker${i} = new kakao.maps.Marker({position: position${i}});
      marker${i}.setMap(map);
      markers.push(marker${i});

      kakao.maps.event.addListener(marker${i}, 'click', (function(marker) {
        return function() {
          onTapMarker.postMessage(marker.getPosition().toString());
        };
      })(marker${i}));
    """;
  }

  // Add control options as you wish
  markerScript += """
    var zoomControl = new kakao.maps.ZoomControl();
    map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);

    var mapTypeControl = new kakao.maps.MapTypeControl();
    map.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);
  """;

  return markerScript;
}

String generateBoundsScript(List<dynamic> searchResults) {
  String pointsScript = searchResults.map((result) {
    return "new kakao.maps.LatLng(${result['lat']}, ${result['lng']})";
  }).join(", ");

  return """
    var bounds = new kakao.maps.LatLngBounds();
    var points = [$pointsScript];
    for (var i = 0; i < points.length; i++) {
      bounds.extend(points[i]);
    }
    map.setBounds(bounds);
  """;
}