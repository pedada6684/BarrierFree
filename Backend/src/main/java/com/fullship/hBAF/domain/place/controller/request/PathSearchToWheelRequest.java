package com.fullship.hBAF.domain.place.controller.request;

import com.fullship.hBAF.global.api.response.TransitPathForm.Legs;
import com.fullship.hBAF.global.api.service.command.SearchPathToWheelCommand;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class PathSearchToWheelRequest {

  private String startX;
  private String startY;
  private String endX;
  private String endY;

  public static SearchPathToWheelCommand createForWheel(Legs legs) {
    return PathSearchToWheelRequest.builder()
        .startX(legs.getStartLon())
        .startY(legs.getStartLat())
        .endX(legs.getEndLon())
        .endY(legs.getEndLat())
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
