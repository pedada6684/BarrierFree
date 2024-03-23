String generateMarkerScript(List<dynamic> searchResults) {
  String markerScript = "var markers = [];";

  for (var i = 0; i < searchResults.length; i++) {
    var result = searchResults[i];
    markerScript += """
      var position${i} = new kakao.maps.LatLng(${result['y']}, ${result['x']});
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

// ...

// KakaoMapView(
// width: MediaQuery.of(context).size.width,
// height: 400,
// kakaoMapKey: kakaoMapKey,
// lat: 33.450701,
// lng: 126.570667,
// customScript: generateMarkerScript(searchResults),
// onTapMarker: (message) {
// ScaffoldMessenger.of(context)
//     .showSnackBar(SnackBar(content: Text(message.message)));
// },
// );
