package com.fullship.hBAF.domain.place.controller.request;

import com.fullship.hBAF.global.api.response.PointGeoCode;
import com.fullship.hBAF.global.api.service.command.SearchPathToWheelCommand;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PathSearchToWheelRequest {

  private String startLng;
  private String startLat;
  private String endLng;
  private String endLat;

  public static SearchPathToWheelCommand createForWheel(PointGeoCode startGeo, PointGeoCode endGeo) {
    return PathSearchToWheelRequest.builder()
        .startLng(startGeo.getLongitude())
        .startLat(startGeo.getLatitude())
        .endLng(endGeo.getLongitude())
        .endLat(endGeo.getLatitude())
        .build()
        .createForWheel();
  }

  public SearchPathToWheelCommand createForWheel() {
    try {
      return SearchPathToWheelCommand.builder()
          .uri(new URI("https://apis.openapi.sk.com/tmap/routes/pedestrian"))
          .requestBody(createRequestBody())
          .build();
    } catch (URISyntaxException e) {
      throw new CustomException(ErrorCode.URI_SYNTAX_ERROR);
    }
  }

  public Map<String, Object> createRequestBody() {
    Map<String, Object> map = new HashMap<>();
    map.put("startX", startLng);
    map.put("startY", startLat);
    map.put("endX", endLng);
    map.put("endY", endLat);
    map.put("startName", "출발지");
    map.put("endName", "도착지");
    map.put("searchOption", "30");

    return map;
  }
}
