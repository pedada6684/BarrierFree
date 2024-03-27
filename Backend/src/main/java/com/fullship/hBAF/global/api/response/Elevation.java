package com.fullship.hBAF.global.api.response;

import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class Elevation {

  List<String[]> geoCode;

  public static Elevation jsonToO(ResponseEntity<String> response) {
    List<String[]> geoCode = new ArrayList<>();
    try {
      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(response.getBody());
      JSONArray results = (JSONArray) object.get("results");
      for (Object o : results) {
        JSONObject result = (JSONObject) o;
        JSONObject location = (JSONObject) result.get("location");
        geoCode.add(
            new String[]{
                location.get("lng").toString(),
                location.get("lat").toString(),
                result.get("elevation").toString()
            });
      }
    } catch (ParseException e) {
      throw new CustomException(ErrorCode.JSON_PARSE_IMPOSSIBLE);
    }
    return Elevation.builder().geoCode(geoCode).build();
  }
}
