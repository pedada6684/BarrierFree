package com.fullship.hBAF.global.api.response;

import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import lombok.Builder;
import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;

@Data
@Builder
@Schema(description = "대중교통 경로 탐색 결과")
public class TransitPathForm {

  /*==========전체 경로 정보===========*/
  @Schema(description = "환승 횟수")
  private long transferCount;
  @Schema(description = "전체 이동 거리")
  private long totalDistance;
  @Schema(description = "전체 소요 시간")
  private long totalTime;
  @Schema(description = "전체 도보 거리")
  private long totalWalkDistance;
  @Schema(description = "전체 도봉 이동 시간")
  private long totalWalkTime;
  @Schema(description = "전체 요금")
  private long totalFare;

  @Schema(description = "구간 정보")
  private List<Legs> legs;

  @Data
  @Builder
  public static class Legs {

    /*=============구간 정보=============*/
    @Schema(description = "WALK, SUBWAY, BUS")
    private String mode;
    @Schema(description = "구간 소요 시간")
    private long sectionTime;
    @Schema(description = "구간 거리")
    private long distance;

    /*==========구간 출발지 정보==========*/
    @Schema(description = "구간 출발지")
    private String startName;
    @Schema(description = "구간 출발지 위도")
    private String startLat;
    @Schema(description = "구간 출발지 경도")
    private String startLon;

    /*==========구간 도착지 정보==========*/
    @Schema(description = "구간 도착지")
    private String endName;
    @Schema(description = "구간 도착지 위도")
    private String endLat;
    @Schema(description = "구간 도착지 경도")
    private String endLon;

    @Schema(description = "탑승 가능 버스 리스트")
    private List<String[]> busList;

    @Schema(description = "정류장 개수")
    private long stationCount;

    @Schema(description = "이동 좌표")
    private List<String[]> geoCode;
  }

  /**
   * JSON 결과물 Parsing
   *
   * @param result API 호출 결과물
   * @return 객체화 된 결과물
   * @throws ParseException
   */
  public static List<TransitPathForm> jsonToO(ResponseEntity<String> result) {
    List<TransitPathForm> pathList = new ArrayList<>();
    try {
      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(result.getBody());
      JSONObject metaData = (JSONObject) object.get("metaData");
      JSONObject plan = (JSONObject) metaData.get("plan");
      JSONArray itineraries = (JSONArray) plan.get("itineraries");

      for (int i = 0; i < itineraries.size(); i++) {
        List<Legs> legsList = new ArrayList<>();

        object = (JSONObject) itineraries.get(i);
        JSONObject fare = (JSONObject) object.get("fare");
        JSONObject regular = (JSONObject) fare.get("regular");
        JSONArray legs = (JSONArray) object.get("legs");

        for (int j = 0; j < legs.size(); j++) {
          List<String[]> geoCode = new ArrayList<>();

          JSONObject path = (JSONObject) legs.get(j);
          JSONObject start = (JSONObject) path.get("start");
          JSONObject end = (JSONObject) path.get("end");

          long stationCount = 0;

          /* 휠체어 이동 경로 좌표 */
          if (path.get("mode").equals("WALK")) {
            JSONArray steps = (JSONArray) path.get("steps");
            /* 이동경로가 하나여서 steps 없는 경우 제외 */
            if (steps == null) {
              JSONObject passShape = (JSONObject) path.get("passShape");
              /* 도착지여서 이동 없는 경우 제외 */
              if (passShape != null) {
                String geo = (String) passShape.get("linestring");
                StringTokenizer st = new StringTokenizer(geo, ", ");
                while (st.countTokens() != 0) {
                  geoCode.add(new String[]{st.nextToken(), st.nextToken()});
                }
              }
            } else {
              for (int s = 0; s < steps.size(); s++) {
                JSONObject step = (JSONObject) steps.get(s);
                getGeo(geoCode, step);
                String geo = (String) step.get("linestring");
                StringTokenizer st = new StringTokenizer(geo, ", ");
                while (st.countTokens() != 0) {
                  geoCode.add(new String[]{st.nextToken(), st.nextToken()});
                }
              }
            }
          }
          /* 버스, 지하철의 이동 경로 좌표 */
          else {
            JSONObject passStopList = (JSONObject) path.get("passStopList");
            JSONArray stationList = (JSONArray) passStopList.get("stationList");
            stationCount = stationList.size();

            JSONObject passShape = (JSONObject) path.get("passShape");
            String geo = (String) passShape.get("linestring");
            StringTokenizer st = new StringTokenizer(geo, ", ");
            while (st.countTokens() != 0) {
              geoCode.add(new String[]{st.nextToken(), st.nextToken()});
            }
          }

          List<String[]> busList = new ArrayList<>();
          if (path.get("mode").equals("BUS")) {
            JSONArray lane = (JSONArray) path.get("Lane");
            if (lane != null) {
              for (int b = 0; b < lane.size(); b++) {
                JSONObject bus = (JSONObject) lane.get(b);
                busList.add(new String[]{(String) bus.get("route"), (String) bus.get("routeId")});
              }
            } else {
              busList.add(new String[]{(String) path.get("route"), (String) path.get("routeId")});
            }
          }

          Legs leg = Legs.builder()
              .mode((String) path.get("mode"))
              .sectionTime((long) path.get("sectionTime"))
              .distance((long) path.get("distance"))
              .startName((String) start.get("name"))
              .startLat(String.valueOf(start.get("lat")))
              .startLon(String.valueOf(start.get("lon")))
              .endName((String) end.get("name"))
              .endLat(String.valueOf(end.get("lat")))
              .endLon(String.valueOf(end.get("lon")))
              .geoCode(geoCode)
              .stationCount(stationCount)
              .busList(busList)
              .build();

          legsList.add(leg);
        }

        TransitPathForm transitPathForm =
            TransitPathForm.builder()
                .totalFare((long) regular.get("totalFare"))
                .transferCount((long) object.get("transferCount"))
                .totalDistance((long) object.get("totalDistance"))
                .totalTime((long) object.get("totalTime"))
                .totalWalkDistance((long) object.get("totalWalkDistance"))
                .totalWalkTime((long) object.get("totalWalkTime"))
                .legs(legsList)
                .build();

        pathList.add(transitPathForm);
      }

      return pathList;
    } catch (ParseException p) {
      throw new CustomException(ErrorCode.JSON_PARSE_IMPOSSIBLE);
    }
  }

  private static void getGeo(List<String[]> geoCode, JSONObject object) {
    String geo = (String) object.get("linestring");
    StringTokenizer st = new StringTokenizer(geo, ", ");
    while (st.countTokens() != 0) {
      geoCode.add(new String[]{st.nextToken(), st.nextToken()});
    }
  }
}
