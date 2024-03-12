package com.fullship.hBAF.domain.place.controller.request;

import com.fullship.hBAF.global.api.service.command.SearchPathToTransitCommand;
import com.fullship.hBAF.global.api.service.command.SearchPathToWheelCommand;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.net.URI;
import java.net.URISyntaxException;
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
    try {
      return SearchPathToTransitCommand.builder()
          .uri(new URI("https://apis.openapi.sk.com/transit/routes"))
          .requestBody(createRequestBody())
          .build();
    } catch (URISyntaxException e) {
      throw new CustomException(ErrorCode.URI_SYNTAX_ERROR);
    }
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
