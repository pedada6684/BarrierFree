package com.fullship.hBAF.domain.place.controller.request;

import com.fullship.hBAF.global.api.service.command.SearchPathToWheelCommand;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class PathSearchToWheelRequest {

  private Float startX;
  private Float startY;
  private Float endX;
  private Float endY;

  public SearchPathToWheelCommand createForWheel() {
    return SearchPathToWheelCommand.builder()
        .url("https://apis.openapi.sk.com/tmap/routes/pedestrian")
        .requestBody(createRequestBody())
        .build();
  }

  public Map createRequestBody() {
    Map<String, Object> map = new HashMap<>();
    map.put("startX", startX);
    map.put("startY", startY);
    map.put("endX", endX);
    map.put("endY", endY);
    map.put("startName", "출발지");
    map.put("endName", "도착지");
    map.put("searchOption", "30");

    return map;
  }
}
