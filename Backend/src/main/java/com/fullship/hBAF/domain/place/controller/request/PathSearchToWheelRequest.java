package com.fullship.hBAF.domain.place.controller.request;

import com.fullship.hBAF.global.api.response.PointGeoCode;
import com.fullship.hBAF.domain.place.service.command.SearchPathToWheelCommand;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PathSearchToWheelRequest {

  private final int wheel = 4;
  private final int eWheel = 10;
  private final int crutch = 3;

  private String type;
  private String startLng;
  private String startLat;
  private String endLng;
  private String endLat;

  public static SearchPathToWheelCommand createForWheel(PointGeoCode startGeo, PointGeoCode endGeo, String type) {
    return PathSearchToWheelRequest.builder()
        .startLng(startGeo.getLongitude())
        .startLat(startGeo.getLatitude())
        .endLng(endGeo.getLongitude())
        .endLat(endGeo.getLatitude())
        .type(type)
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
    double dstartLng = Double.parseDouble(startLng);
    double dstartLat = Double.parseDouble(startLat);
    DecimalFormat df = new DecimalFormat("#.####");

    Map<String, Object> map = new HashMap<>();
    map.put("speed", type.equals("휠체어") ? wheel : type.equals("전동휠체어") ? eWheel : crutch);
    map.put("startX", df.format(dstartLng));
    map.put("startY", df.format(dstartLat));
    map.put("endX", endLng);
    map.put("endY", endLat);
    map.put("startName", "출발지");
    map.put("endName", "도착지");
    map.put("searchOption", "30");

    return map;
  }
}
