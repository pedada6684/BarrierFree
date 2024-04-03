package com.fullship.hBAF.global.api.response;

import static com.fullship.hBAF.global.api.response.SimplifyGeoCode.polyLineSimplify;

import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;

@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PathGeoCode {

  @Schema(description = "경로 구분")
  private long trafficType;
  @Schema(description = "좌표 값")
  private List<GeoCode> geoCode;

  public static List<PathGeoCode> jsonToO(ResponseEntity<String> response) {
    List<PathGeoCode> pathGeoCodes = new ArrayList<>();
    try {
      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(response.getBody());
//      log.info("pathResult = {}", object);
      JSONObject result = (JSONObject) object.get("result");
      JSONArray lanes = (JSONArray) result.get("lane");
      for (Object o : lanes) {
        JSONObject lane = (JSONObject) o;
        long trafficType = (long) lane.get("class");

        List<String[]> geoCode = new ArrayList<>();
        JSONObject section = (JSONObject) ((JSONArray) lane.get("section")).get(0);
        JSONArray graphPoses = (JSONArray) section.get("graphPos");
        for (Object graphPose : graphPoses) {
          JSONObject graphPos = (JSONObject) graphPose;
          geoCode.add(
              new String[]{graphPos.get("x").toString(), graphPos.get("y").toString()});
        }

        List<GeoCode> compressCode = polyLineSimplify(geoCode, 0.0005);

        pathGeoCodes.add(
            PathGeoCode.builder()
                .trafficType(trafficType == 1 ? 2 : 1)
                .geoCode(compressCode)
                .build());
      }
      return pathGeoCodes;
    } catch (ParseException e) {
      throw new CustomException(ErrorCode.JSON_PARSE_IMPOSSIBLE);
    }

  }
}
