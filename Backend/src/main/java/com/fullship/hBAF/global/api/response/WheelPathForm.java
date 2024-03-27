package com.fullship.hBAF.global.api.response;

import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;

@Data
@Slf4j
@Builder
public class WheelPathForm {

  private List<GeoCode> geoCode;
  private long totalDistance;
  private long totalTime;
  private String startName;
  private String startLat;
  private String startLon;
  private String endName;
  private String endLat;
  private String endLon;

  public static WheelPathForm jsonToO(ResponseEntity<String> result) {
    List<GeoCode> geoCode = new ArrayList<>();
    log.info("result = {}", result.getBody());
    try {
      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(result.getBody());
      JSONArray features = (JSONArray) object.get("features");

      for (Object o : features) {
        JSONObject feature = (JSONObject) o;
        JSONObject geometry = (JSONObject) feature.get("geometry");
        JSONArray coordinates = (JSONArray) geometry.get("coordinates");

        for (Object value : coordinates) {
          if (coordinates.get(0) instanceof Double) {
            geoCode.add(GeoCode.builder()
                .longitude(String.valueOf(coordinates.get(0)))
                .latitude(String.valueOf(coordinates.get(1))).build());
            break;
          }
          JSONArray coordinate = (JSONArray) value;
          if (geoCode.get(geoCode.size() - 1).getLongitude().equals(String.valueOf(coordinates.get(0)))
              && geoCode.get(geoCode.size() - 1).getLatitude().equals(String.valueOf(coordinates.get(1)))) {
            continue;
          }
          geoCode.add(GeoCode.builder()
              .longitude(String.valueOf(coordinate.get(0)))
              .latitude(String.valueOf(coordinate.get(1))).build());
        }
      }

//      geoCode = getCompress(geoCode, 10);
//      geoCode = polyLineSimplify(geoCode, 0.00005);

      JSONObject startFeature = (JSONObject) features.get(0);
      JSONObject startGeometry = (JSONObject) startFeature.get("geometry");
      JSONArray startGeo = (JSONArray) startGeometry.get("coordinates");

      JSONObject endFeature = (JSONObject) features.get(features.size() - 1);
      JSONObject endGeometry = (JSONObject) endFeature.get("geometry");
      JSONArray endGeo = (JSONArray) endGeometry.get("coordinates");

      JSONObject properties = (JSONObject) startFeature.get("properties");

      return WheelPathForm.builder()
          .totalDistance((long) properties.get("totalDistance"))
          .totalTime((long) properties.get("totalTime"))
          .startLon(String.valueOf(startGeo.get(0)))
          .startLat(String.valueOf(startGeo.get(1)))
          .endLon(String.valueOf(endGeo.get(0)))
          .endLat(String.valueOf(endGeo.get(1)))
          .geoCode(geoCode)
          .build();
    } catch (ParseException p) {
      log.error("Parsing 실패");
      throw new CustomException(ErrorCode.JSON_PARSE_IMPOSSIBLE);
    }
  }
}
