package com.fullship.hBAF.global.api.response;

import com.fullship.hBAF.global.api.response.OdSayPath.SubPath.Bus;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "대중교통 경로 탐색 결과")
public class OdSayPath {

  @Schema(description = "경로 타입 (버스: 1, 지하철: 2, 혼합: 3)")
  private long pathType;
  @Schema(description = "도보 제외 이동 거리")
  private double transitDistance;
  @Schema(description = "도보 이동 거리")
  private long walkDistance;
  @Schema(description = "소요 시간")
  private long totalTime;
  @Schema(description = "도보 시간")
  private long walkTime;
  @Schema(description = "교통비")
  private long payment;
  @Schema(description = "출발지")
  private String startName;
  @Schema(description = "도착지")
  private String endName;
  @Schema(description = "세부 경로")
  private List<SubPath> subPaths;
  @Schema(description = "경로 정보")
  private String mapObj;
  @Schema(description = "버스 환승 횟수")
  private long busTransferCount;
  @Schema(description = "지하철 환승 횟수")
  private long subwayTransferCount;
  @Schema(description = "총 환승 횟수")
  private long totalTransferCount;
  @Schema(description = "경로 좌표")
  private List<PathGeoCode> geoCode;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class SubPath {

    /* 경로 정보 */
    @Schema(description = "경로 타입")
    private long trafficType;
    @Schema(description = "이동 거리")
    private long sectionDistance;
    @Schema(description = "소요 시간")
    private long sectionTime;
    @Schema(description = "상, 하행 정보 (상행: 1, 하행: 2)")
    private long wayCode;
    @Schema(description = "대기 시간")
    private long waitTime;
    /* 정류장 정보 */
    @Schema(description = "정류장 개수")
    private long stationCount;
    @Schema(description = "출발 정류장")
    private String startStationName;
    @Schema(description = "출발 좌표")
    private PointGeoCode startGeo;
    @Schema(description = "도착 정류장")
    private String endStationName;
    @Schema(description = "도착 좌표")
    private PointGeoCode endGeo;
    /* 경유 정류장 정보 */
    @Schema(description = "경유 정류장")
    private List<String> passStation;
    @Schema(description = "경유 정류장 좌표")
    private List<PointGeoCode> passStationGeo;
    @Schema(description = "버스 정보")
    private List<Bus> busList;
    /* 버스 정류장 정보 */
    @Schema(description = "출발 버스 정류장 arsId")
    private String startArsId;
    @Schema(description = "출발 버스 정류장 localId")
    private String startLocalId;
    @Schema(description = "출발 버스 정류장 OdSayId")
    private String startStationId;
    @Schema(description = "도착 버스 정류장 arsId")
    private String endArsId;
    @Schema(description = "도착 버스 정류장 localId")
    private String endLocalId;
    @Schema(description = "도착 버스 정류장 OdSayId")
    private String endStationId;
    @Schema(description = "경유 버스 정류장 arsId")
    private List<String> passArsId;
    @Schema(description = "경유 버스 정류장 localId")
    private List<String> passLocalId;
    @Schema(description = "경유 버스 정류장 OdSayId")
    private List<String> passStationId;
    @Schema(description = "남은 정류장 수")
    private long beforeCount;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Bus {

      /* 버스 정보 */
      @Schema(description = "버스 번호")
      private String busNo;
      @Schema(description = "버스 종류")
      private String busType;
      @Schema(description = "버스 식별번호")
      private String busId;
      @Schema(description = "버스 공공 식별번호")
      private String publicBusId;
    }

  }

  public static SubPath changeSubPathToWheel(WheelPathForm wheelPath) {
    return SubPath.builder()
        .trafficType(3)
        .startStationName(wheelPath.getStartName())
        .startGeo(
            PointGeoCode.builder()
                .longitude(wheelPath.getStartLon())
                .latitude(wheelPath.getStartLat())
                .build())
        .endStationName(wheelPath.getEndName())
        .endGeo(PointGeoCode.builder()
            .longitude(wheelPath.getEndLon())
            .latitude(wheelPath.getEndLat())
            .build())
        .sectionDistance(wheelPath.getTotalDistance())
        .sectionTime(wheelPath.getTotalTime() / 60)
        .build();
  }

  /**
   * JSON 결과물 Parsing
   *
   * @param result API 호출 결과물
   * @return 객체화 된 결과물
   */
  public static List<OdSayPath> jsonToO(ResponseEntity<String> result) {
    List<OdSayPath> pathList = new ArrayList<>();
    try {
      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(result.getBody());
      JSONObject resulT = (JSONObject) object.get("result");
      JSONArray path = (JSONArray) resulT.get("path");
      for (Object o : path) {
        List<SubPath> subPathList = new ArrayList<>();
        object = (JSONObject) o;
        JSONObject info = (JSONObject) object.get("info");
        JSONArray subPaths = (JSONArray) object.get("subPath");

        for (Object value : subPaths) {
          List<String> passStation = new ArrayList<>();
          List<PointGeoCode> passStationGeo = new ArrayList<>();
          JSONObject passStopList;
          JSONArray stations;

          JSONObject subPath = (JSONObject) value;
          long trafficType = (long) subPath.get("trafficType");
          switch ((int) trafficType) {
            /* 지하철 */
            case 1:
              passStopList = (JSONObject) subPath.get("passStopList");
              stations = (JSONArray) passStopList.get("stations");
              for (Object element : stations) {
                JSONObject station = (JSONObject) element;
                passStation.add(station.get("stationName").toString());
                passStationGeo.add(
                    PointGeoCode.builder()
                        .longitude(station.get("x").toString())
                        .latitude(station.get("y").toString())
                        .build());
              }
              subPathList.add(
                  SubPath.builder()
                      .trafficType(trafficType)
                      .sectionDistance((long) subPath.get("distance"))
                      .sectionTime((long) subPath.get("sectionTime"))
                      .wayCode((long) subPath.get("wayCode"))
                      .stationCount((long) subPath.get("stationCount"))
                      .startStationName(subPath.get("startName").toString())
                      .startGeo(PointGeoCode.builder().longitude(subPath.get("startX").toString())
                          .latitude(subPath.get("startY").toString()).build())
                      .endStationName(subPath.get("endName").toString())
                      .endGeo(PointGeoCode.builder().longitude(subPath.get("endX").toString())
                          .latitude(subPath.get("endY").toString()).build())
                      .passStation(passStation)
                      .passStationGeo(passStationGeo)
                      .build());
              break;
            /* 버스 */
            case 2:
              JSONArray lanes = (JSONArray) subPath.get("lane");
              List<Bus> busList = new ArrayList<>();
              for (Object item : lanes) {
                JSONObject lane = (JSONObject) item;
                busList.add(Bus.builder()
                    .busNo(lane.get("busNo").toString())
                    .busType(lane.get("type").toString())
                    .busId(lane.get("busID").toString())
                    .publicBusId(initId(lane.get("busLocalBlID"), 2))
                    .build());
              }
              List<String> passArsId = new ArrayList<>();
              List<String> passLocalId = new ArrayList<>();
              List<String> passStationId = new ArrayList<>();
              passStopList = (JSONObject) subPath.get("passStopList");
              stations = (JSONArray) passStopList.get("stations");
              for (int m = 1; m < stations.size() - 1; m++) {
                JSONObject station = (JSONObject) stations.get(m);
                passStation.add(station.get("stationName").toString());
                passStationGeo.add(
                    PointGeoCode.builder()
                        .longitude(station.get("x").toString())
                        .latitude(station.get("y").toString())
                        .build());
                passArsId.add(initId(station.get("arsID"), 1));
                passLocalId.add(initId(station.get("localStationID"), 2));
                passStationId.add(initId(station.get("stationID"), 3));
              }
              subPathList.add(
                  SubPath.builder()
                      .trafficType(trafficType)
                      .sectionDistance((long) subPath.get("distance"))
                      .sectionTime((long) subPath.get("sectionTime"))
                      .stationCount((long) subPath.get("stationCount"))
                      .startStationName(subPath.get("startName").toString())
                      .startGeo(PointGeoCode.builder().longitude(subPath.get("startX").toString())
                          .latitude(subPath.get("startY").toString()).build())
                      .endStationName(subPath.get("endName").toString())
                      .endGeo(PointGeoCode.builder().longitude(subPath.get("endX").toString())
                          .latitude(subPath.get("endY").toString()).build())
                      .passStation(passStation)
                      .passStationGeo(passStationGeo)
                      .busList(busList)
                      .startArsId(initId(subPath.get("startArsID"), 1))
                      .startLocalId(initId(subPath.get("startLocalStationID"), 2))
                      .startStationId(initId(subPath.get("startID"), 3))
                      .endArsId(initId(subPath.get("endArsID"), 1))
                      .endLocalId(initId(subPath.get("endLocalStationID"), 2))
                      .endStationId(initId(subPath.get("endID"), 3))
                      .passArsId(passArsId)
                      .passLocalId(passLocalId)
                      .passStationId(passStationId)
                      .build());
              break;
            /* 도보 */
            case 3:
              subPathList.add(
                  SubPath.builder()
                      .trafficType(trafficType)
                      .sectionDistance((long) subPath.get("distance"))
                      .sectionTime((long) subPath.get("sectionTime"))
                      .build());
              break;
          }
        }

        Object busTransferCount = info.get("busTransitCount");
        busTransferCount = busTransferCount != null ? busTransferCount : 0;
        Object subwayTransferCount = info.get("subwayTransitCount");
        subwayTransferCount = subwayTransferCount != null ? subwayTransferCount : 0;

        pathList.add(
            OdSayPath.builder()
                .pathType((long) object.get("pathType"))
                .transitDistance((double) info.get("trafficDistance"))
                .walkDistance((long) info.get("totalWalk"))
                .walkTime((long) info.get("totalWalkTime"))
                .totalTime((long) info.get("totalTime"))
                .payment((long) info.get("payment"))
                .startName(info.get("firstStartStation").toString())
                .endName(info.get("lastEndStation").toString())
                .subPaths(subPathList)
                .mapObj(info.get("mapObj").toString())
                .busTransferCount((long) busTransferCount)
                .subwayTransferCount((long) subwayTransferCount)
                .totalTransferCount(
                    (long) busTransferCount + (long) subwayTransferCount)
                .build());
      }
    } catch (ParseException e) {
      throw new CustomException(ErrorCode.JSON_PARSE_IMPOSSIBLE);
    }
    return pathList;
  }

  private static String initId(Object o, long type) {
    String id = o.toString();
    return switch ((int) type) {
      case 1 -> {
        StringTokenizer st = new StringTokenizer(id, "-");
        yield st.nextToken() + st.nextToken(); // arsID
      }
      case 2 -> // localID
          id.substring(3);
      case 3 -> // stationID
          id;
      default -> null;
    };
  }
}