package com.fullship.hBAF.global.api.response;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

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
public class Elevation {

  List<GeoCode> geoCode;

  public static Elevation jsonToO(ResponseEntity<String> response) {
    List<GeoCode> geoCode = new ArrayList<>();
    try {
      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(response.getBody());
      JSONArray results = (JSONArray) object.get("results");
      for (Object o : results) {
        JSONObject result = (JSONObject) o;
        JSONObject location = (JSONObject) result.get("location");
        geoCode.add(
            GeoCode.builder()
                .longitude(location.get("lng").toString())
                .latitude(location.get("lat").toString())
                .angleSlope(result.get("elevation").toString())
                .build());
      }
    } catch (ParseException e) {
      throw new CustomException(ErrorCode.JSON_PARSE_IMPOSSIBLE);
    }


    for (int i = 0; i < geoCode.size() - 1; i++) {
      geoCode.get(i).setAngleSlope(calculateSlope(geoCode.get(i), geoCode.get(i + 1)));
    }


    geoCode.get(geoCode.size() - 1).setAngleSlope("0");

    return Elevation.builder().geoCode(geoCode).build();
  }

  /**
   * 두 좌표 사이의 거리와 고도 차이를 이용한 경사도 측정
   * @param cur 현재 포인트 좌표 정보
   * @param next 다음 포인트 좌표 정보
   * @return 경사도
   */
  public static String calculateSlope(GeoCode cur, GeoCode next) {
    double R = 6372.8 * 1000;

    double curLat = Double.parseDouble(cur.getLatitude());
    double curLon = Double.parseDouble(cur.getLongitude());
    double curEle = Double.parseDouble(cur.getAngleSlope());
    double nextLat = Double.parseDouble(next.getLatitude());
    double nextLon = Double.parseDouble(next.getLongitude());
    double nextEle = Double.parseDouble(next.getAngleSlope());

    double dLat = Math.toRadians(nextLat - curLat);
    double dLon = Math.toRadians(nextLon - curLon);
    double dEle = nextEle - curEle;

    double a = pow(sin(dLat / 2), 2.0) + pow(sin(dLon / 2), 2.0)
        * cos(Math.toRadians(curLat)) * cos(Math.toRadians(nextLat));
    double c = 2 * asin(sqrt(a));

    double dist = R * c;

    double atan2 = atan2(dEle, dist);
    double angle = atan2 * 180 / Math.PI;

    return String.valueOf(angle);
  }
}
