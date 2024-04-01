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
public class TaxiPathForm {

  String minCost;
  String maxCost;
  String totalDistance;
  String totalTime;
  List<GeoCode> geoCode;

  public static TaxiPathForm jsonToO(ResponseEntity<String> result) {
    List<GeoCode> geoCode = new ArrayList<>();
    try {
      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(result.getBody());
      JSONArray features = (JSONArray) object.get("features");
      JSONObject properties = (JSONObject) ((JSONObject) features.get(0)).get("properties");
      for (Object o : features) {
        JSONObject feature = (JSONObject) o;
        JSONObject geometry = (JSONObject) feature.get("geometry");
        JSONArray coordinates = (JSONArray) geometry.get("coordinates");
        if (coordinates.get(0).getClass() != Double.class) {
          for (Object value : coordinates) {
            JSONArray coordinate = (JSONArray) value;
            geoCode.add(GeoCode.builder()
                .longitude(coordinate.get(0).toString())
                .latitude(coordinate.get(1).toString())
                .build());
          }
        } else {
          geoCode.add(GeoCode.builder()
              .longitude(coordinates.get(0).toString())
              .latitude(coordinates.get(1).toString())
              .build());
        }
      }
      return TaxiPathForm.builder()
          .totalDistance(properties.get("totalDistance").toString())
          .totalTime(properties.get("totalTime").toString())
          .geoCode(geoCode).build();
    } catch (ParseException e) {
      throw new CustomException(ErrorCode.JSON_PARSE_IMPOSSIBLE);
    }
  }
}
