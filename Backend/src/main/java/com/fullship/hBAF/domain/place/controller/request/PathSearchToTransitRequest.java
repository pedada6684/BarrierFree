package com.fullship.hBAF.domain.place.controller.request;

import com.fullship.hBAF.global.api.service.command.SearchPathToTransitCommand;
import com.fullship.hBAF.global.api.service.command.SearchPathToWheelCommand;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class PathSearchToTransitRequest {

  private String startX;
  private String startY;
  private String endX;
  private String endY;

  public SearchPathToTransitCommand createForTransit() {
    return SearchPathToTransitCommand.builder()
        .url("https://apis.openapi.sk.com/transit/routes")
        .requestBody(createRequestBody())
        .build();
  }

  public Map createRequestBody() {
    Map<String, Object> map = new HashMap<>();
    map.put("startX", startX);
    map.put("startY", startY);
    map.put("endX", endX);
    map.put("endY", endY);
    return map;
  }
}
