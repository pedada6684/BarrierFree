package com.fullship.hBAF.domain.place.controller.request;

import com.fullship.hBAF.global.api.service.command.OdSayPathCommand;
import com.fullship.hBAF.domain.place.service.command.SearchPathToTrafficCommand;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class PathSearchToTrafficRequest {

  private String type;
  private String startLng;
  private String startLat;
  private String endLng;
  private String endLat;

  public SearchPathToTrafficCommand createForTaxi() {
    try {
      return SearchPathToTrafficCommand.builder()
          .uri(new URI("https://apis.openapi.sk.com/tmap/routes"))
          .requestBody(createRequestBody())
          .build();
    } catch (URISyntaxException e) {
      throw new CustomException(ErrorCode.URI_SYNTAX_ERROR);
    }
  }

  public OdSayPathCommand createForSearch() {
    try {
      return OdSayPathCommand.builder()
          .type(type)
          .uri("https://api.odsay.com/v1/api/searchPubTransPathT?"
                  + "SX=" + startLng
                  + "&SY=" + startLat
                  + "&EX=" + endLng
                  + "&EY=" + endLat)
          .requestBody(createRequestBody())
          .build();
    } catch (Exception e) {
      throw new CustomException(ErrorCode.URI_SYNTAX_ERROR);
    }
  }

  public Map<String, Object> createRequestBody() {
    Map<String, Object> map = new HashMap<>();
    map.put("startX", startLng);
    map.put("startY", startLat);
    map.put("endX", endLng);
    map.put("endY", endLat);
    return map;
  }
}
