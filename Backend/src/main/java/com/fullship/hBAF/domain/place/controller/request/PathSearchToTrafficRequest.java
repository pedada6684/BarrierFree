package com.fullship.hBAF.domain.place.controller.request;

import com.fullship.hBAF.global.api.service.command.OdSayPathCommand;
import com.fullship.hBAF.global.api.service.command.SearchPathToTrafficCommand;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class PathSearchToTrafficRequest {

  private String startX;
  private String startY;
  private String endX;
  private String endY;

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
          .uri("https://api.odsay.com/v1/api/searchPubTransPathT?"
                  + "SX=" + startX
                  + "&SY=" + startY
                  + "&EX=" + endX
                  + "&EY=" + endY)
          .build();
    } catch (Exception e) {
      throw new CustomException(ErrorCode.URI_SYNTAX_ERROR);
    }
  }

  public Map<String, Object> createRequestBody() {
    Map<String, Object> map = new HashMap<>();
    map.put("startX", startX);
    map.put("startY", startY);
    map.put("endX", endX);
    map.put("endY", endY);
    return map;
  }
}
