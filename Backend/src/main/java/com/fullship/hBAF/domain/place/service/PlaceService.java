package com.fullship.hBAF.domain.place.service;

import static com.fullship.hBAF.domain.place.controller.request.PathSearchToWheelRequest.createForWheel;
import static com.fullship.hBAF.global.api.response.OdSayPath.changeSubPathToWheel;

import com.fullship.hBAF.domain.busInfo.entity.BusInfo;
import com.fullship.hBAF.domain.busInfo.repository.BusInfoRepository;
import com.fullship.hBAF.domain.busStop.entity.BusStop;
import com.fullship.hBAF.domain.busStop.repository.BusStopRepository;
import com.fullship.hBAF.domain.metroInfo.repository.MetroInfoRepository;
import com.fullship.hBAF.domain.place.controller.response.GetPlaceResponse;
import com.fullship.hBAF.domain.place.controller.response.PlaceListResponse;
import com.fullship.hBAF.domain.place.controller.response.PlaceResponse;
import com.fullship.hBAF.domain.place.entity.Image;
import com.fullship.hBAF.domain.place.entity.Place;
import com.fullship.hBAF.domain.place.repository.ImageRepository;
import com.fullship.hBAF.domain.place.repository.PlaceRepository;
import com.fullship.hBAF.domain.place.service.command.Request.AngleSlopeCommand;
import com.fullship.hBAF.domain.place.service.command.Request.GetPlaceListRequestComment;
import com.fullship.hBAF.domain.stationInfo.entity.StationInfo;
import com.fullship.hBAF.domain.stationInfo.repository.StationInfoRepository;
import com.fullship.hBAF.domain.stationStopInfo.entity.StationStopInfo;
import com.fullship.hBAF.domain.stationStopInfo.repository.StationStopInfoRepository;
import com.fullship.hBAF.global.api.response.OdSayPath;
import com.fullship.hBAF.global.api.response.OdSayPath.SubPath;
import com.fullship.hBAF.global.api.response.OdSayPath.SubPath.Bus;
import com.fullship.hBAF.global.api.response.PathGeoCode;
import com.fullship.hBAF.global.api.response.PointGeoCode;
import com.fullship.hBAF.global.api.response.WheelPathForm;
import com.fullship.hBAF.global.api.service.GoogleApiService;
import com.fullship.hBAF.global.api.service.OdSayApiService;
import com.fullship.hBAF.domain.place.service.command.CreatePlaceCommand;
import com.fullship.hBAF.domain.place.service.command.UpdatePlaceImageCommand;
import com.fullship.hBAF.global.api.service.TMapApiService;
import com.fullship.hBAF.global.api.service.TagoApiService;
import com.fullship.hBAF.global.api.response.BusesCurLocation;
import com.fullship.hBAF.global.api.service.command.ElevationForPathCommand;
import com.fullship.hBAF.global.api.service.command.OdSayGeoCommand;
import com.fullship.hBAF.global.api.service.command.OdSayPathCommand;
import com.fullship.hBAF.global.api.service.command.SearchPathToWheelCommand;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;

import java.io.IOException;

import com.fullship.hBAF.global.response.exception.CustomException;
import com.uber.h3core.H3Core;
import com.uber.h3core.util.GeoCoord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.fullship.hBAF.global.response.ErrorCode.TEST_NOT_FOUND;
import static com.fullship.hBAF.util.H3.daejeonH3Index;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

  private final TMapApiService tMapApiService;
  private final OdSayApiService odSayApiService;
  private final TagoApiService tagoApiService;
  private final GoogleApiService googleApiService;
  private final BusInfoRepository busInfoRepository;
  private final BusStopRepository busStopRepository;
  private final PlaceRepository placeRepository;
  private final StationInfoRepository stationInfoRepository;
  private final MetroInfoRepository metroInfoRepository;
  private final StationStopInfoRepository stationStopInfoRepository;
  private final ImageRepository imageRepository;

  public List<OdSayPath> useTransitPath(OdSayPathCommand command) {
    List<OdSayPath> list = odSayApiService.searchPathToTransit(command);
    log.info("대중교통 이용 경로 호출 결과 = {}", list);

    /* 총합 시간 순 정렬 (오름차순) */
    list.sort((o1, o2) -> (int) (o1.getTotalTime() - o2.getTotalTime()));
    /* 소요시간의 중앙값 */
    long midTime = list.get(list.size() / 2).getTotalTime();
    /* 소요시간의 이상치 (중앙값보다 3600 이상 큰 값) 제거 */
    for (int i = (list.size() / 2); i < list.size(); i++) {
      if (list.get(i).getTotalTime() >= midTime + 3600) {
        list = list.subList(0, i);
      }
    }
    log.info("소요시간 이상치 제거 결과 = {}", list);

    /* 환승 횟수 정렬 (오름차순) */
    list.sort((o1, o2) -> (int) (o1.getTotalTransferCount() - o2.getTotalTransferCount()));
    /* 환승 횟수가 최소치 보다 2 이상 높은 경우 제거 */
    long min = list.get(0).getTotalTransferCount();
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getTotalTransferCount() >= min + 2) {
        list = list.subList(0, i);
        break;
      }
    }
    log.info("환승 횟수 이상치 제거 결과 = {}", list);

    for (OdSayPath odSayPath : list) {
      /* 대중교통 경로 좌표 */
      odSayPath.setGeoCode(
          odSayApiService.getOdSayGeoCode(
              OdSayGeoCommand.builder()
                  .uri("https://api.odsay.com/v1/api/loadLane?")
                  .mapObj("0:0@" + odSayPath.getMapObj())
                  .build()));

      int sumTime = 0;
      for (int j = 0; j < odSayPath.getSubPaths().size(); j++) {
        SubPath subPath = odSayPath.getSubPaths().get(j);
        /* WALK 모드 */
        if (subPath.getTrafficType() == 3) {
          PointGeoCode startGeo;
          PointGeoCode endGeo;
          /* 제일 처음에 걷는 경우 */
          if (j == 0) {
            startGeo = PointGeoCode.builder()
                .longitude(command.getRequestBody().get("startX").toString())
                .latitude(command.getRequestBody().get("startY").toString()).build();
            endGeo = odSayPath.getSubPaths().get(j + 1).getStartGeo();
          }
          /* 끝에 걷는 경우 */
          else if (j == odSayPath.getSubPaths().size() - 1) {
            startGeo = odSayPath.getSubPaths().get(j - 1).getEndGeo();
            endGeo = PointGeoCode.builder()
                .longitude(command.getRequestBody().get("endX").toString())
                .latitude(command.getRequestBody().get("endY").toString()).build();
          }
          /* 중간에 걷는 경우 */
          else {
            startGeo = odSayPath.getSubPaths().get(j - 1).getEndGeo();
            endGeo = odSayPath.getSubPaths().get(j + 1).getStartGeo();
          }

          /* 도보 경로가 없는 경우 */
          if (startGeo.getLongitude().equals(endGeo.getLongitude())
              && startGeo.getLatitude().equals(endGeo.getLatitude())) {
            odSayPath.setGeoCode(setWheelGeoCode(odSayPath.getGeoCode(), null, j));
          } else {
            WheelPathForm wheelForm = tMapApiService.searchPathToWheel(
                createForWheel(startGeo, endGeo, command.getType()));
            odSayPath.getSubPaths().set(j, changeSubPathToWheel(wheelForm));
            odSayPath.setGeoCode(setWheelGeoCode(odSayPath.getGeoCode(), wheelForm, j));
          }
        }
        /* BUS 모드 */
        else if (subPath.getTrafficType() == 2) {
          /* 탑승 가능한 Bus 목록 */
          for (Bus bus : subPath.getBusList()) {
            /* 버스 고르기 해야 함 */
            // 1. 상 하행 구분
            log.info("stopId = {}", subPath.getStartStationId());
            log.info("busNo = {}", bus.getBusNo());

            BusStop busStop = busStopRepository.findBusStopByStopIdAndBusId(
                subPath.getStartStationId(), bus.getBusId());
            String direction = busStop.getStopDirection();
            // 2. 검색 대상 노선 찾기
            String busPublicId = bus.getPublicBusId();
            // 3. 해당 노선에 버스 목록 찾기 (상 하행 필터링)
            List<BusesCurLocation> buses =
                tagoApiService.findBusesByPublicId(busPublicId, direction);
            List<BusesCurLocation> lowFilter = new ArrayList<>();
            // 4. 저상 필터링
            for (BusesCurLocation lowBus : buses) {
              BusInfo busInfo =
                  busInfoRepository.findBusInfoByBusRegNo(lowBus.getLicense());
              if (busInfo.getBusType().equals("2")) {
                lowFilter.add(lowBus);
              }
            }
            // 5. 몇 정류장 전에 있는지 확인
            for (BusesCurLocation busesCurLocation : lowFilter) {
              BusStop curBusStop =
                  busStopRepository.findBusStopByArsIdAndBusId(busesCurLocation.getArsId(),
                      bus.getBusId());
              long countStop = Math.abs(busStop.getId() - curBusStop.getId());
              odSayPath.getSubPaths().get(j).setBeforeCount(countStop);
            }
          }
        }
        /* SUBWAY 모드 */
        else if (subPath.getTrafficType() == 1) {
          /* 상행 하행 구분 */
          StationInfo sStation = stationInfoRepository.findStationInfoBySubwayName(
              subPath.getStartStationName());
          StationInfo eStation = stationInfoRepository.findStationInfoBySubwayName(
              subPath.getEndStationName());
          boolean isUp = sStation.getId() - eStation.getId() < 0;

          /* 평일 휴일 구분 */
          LocalDateTime curDateTime = LocalDateTime.now();
          String week = curDateTime.getDayOfWeek()
              .getDisplayName(TextStyle.NARROW, new Locale("ko", "KR"));
          boolean isWeekDay = !week.equals("토") && !week.equals("일");

          LocalTime futureTime = LocalTime.of(curDateTime.getHour(), curDateTime.getMinute());
          futureTime.plusMinutes(sumTime / 60);
          /* 평-휴일, 상-하행 필터를 거치고 남는 metro */
          List<Integer> metroIdList = isUp ?
              metroInfoRepository.findAllByIsWeekDayAndUp(isWeekDay) :
              metroInfoRepository.findAllByIsWeekDayAndDown(isWeekDay);
          String fTime = futureTime.toString();
          fTime = fTime.substring(0, 2) + fTime.substring(3);
          List<StationStopInfo> metroArrInfoList =
              stationStopInfoRepository.findByTimeAndMetroInfo(fTime, metroIdList);

          /* 역 도착시간과 열차 도착시간 비교 (대기시간) */
          String arrTime = metroArrInfoList.get(0).getArrTime();
          LocalTime time = LocalTime.of(
              Integer.parseInt(arrTime.substring(0, 2)),
              Integer.parseInt(arrTime.substring(2)));
          Duration duration = Duration.between(time, futureTime);
          long waitTime = duration.toMinutes() * 60;
          odSayPath.getSubPaths().get(j).setWaitTime(waitTime);
        }
        sumTime += (int) subPath.getSectionTime();
      }
    }

    return list;
  }

  @Transactional(readOnly = false)
  public Long createPlace(CreatePlaceCommand command) {
    //poiId를 통해 존재확인
    if (placeRepository.existsByPoiId(command.getPoiId())) {
      return null;
//      throw new IllegalStateException("이미 존재하는 장소 :" + command.getPoiId()+" "+command.getPlaceName());
    }
    Place newPlace = Place.createNewPlace(
        command.getPlaceName(),
        command.getAddress(),
        command.getLatitude(),
        command.getLongitude(),
        command.getPoiId(),
        command.getCategory(),
        command.getBarrierFree(),
        command.getWtcltId(),
        command.getType()
    );

    if (command.getType()) {
      newPlace.insertWtcltId(command.getWtcltId());
    }
    //검색결과를 기반으로 save함
    return placeRepository.save(newPlace).getId();
  }

  @Transactional(readOnly = false)
  public Long updatePlaceImageUrl(UpdatePlaceImageCommand command) {
    Place place = placeRepository.findById(command.getPlaceId())
        .orElseThrow(() -> new IllegalStateException());
    Optional<Image> imageOptional = imageRepository.findByPlaceAndImageType(place, 0);
    if (imageOptional.isPresent()) {
      Image image = imageOptional.get();
      image.updateImageUrl(command.getImageUrl());
    } else { // 썸네일 이미지 없는 생성
      Image newImage = Image.createNewImage(place, command.getImageUrl(), 0);
      imageRepository.save(newImage);
    }
    return place.getId();
  }

  public Double calculateCost(String[][] arr) throws IOException {
    H3Core h3 = H3Core.newInstance();
    GeoCoord coord = new GeoCoord(Double.parseDouble(arr[arr.length - 1][1]),
        Double.parseDouble(arr[arr.length - 1][0]));
    long lastH3Index = h3.geoToH3(coord.lat, coord.lng, 8);

    int clockIdx = arr.length - 1;
    if (!daejeonH3Index.contains(lastH3Index)) {
      clockIdx = findClock(arr);
    }

    int cost = 1000;
    double totalDis = 0;
    double totalCostDis = -3000;
    double totalClockDis = 0;
    double preLat = Double.parseDouble(arr[0][1]);
    double preLng = Double.parseDouble(arr[0][0]);
    for (int i = 1; i < arr.length; i++) {
      double curLat = Double.parseDouble(arr[i][1]);
      double curLng = Double.parseDouble(arr[i][0]);

      double dis = calculateDistance(preLat, preLng, curLat, curLng);
      totalDis += dis;
      if (i > clockIdx) {
        totalCostDis += dis * 1.2;
        totalClockDis += dis;
      } else {
        totalCostDis += dis;
      }

      preLat = curLat;
      preLng = curLng;
    }
    totalCostDis = Math.max(0, totalCostDis);
    System.out.println("토탈 거리 : " + totalDis);
    System.out.println("시계 위치 : " + arr[clockIdx][1] + " " + arr[clockIdx][0]);
    System.out.println("시계 거리 : " + totalClockDis);
    System.out.println("금액 : " + (cost + totalCostDis * 100 / 440));
    return (cost + totalCostDis * 100 / 440);
  }

  public int findClock(String[][] arr) throws IOException {
    int l = 0;
    int r = arr.length - 1;

    H3Core h3 = H3Core.newInstance();

    while (l < r) {
      int mid = (l + r) >>> 1;

      GeoCoord coord = new GeoCoord(Double.parseDouble(arr[mid][1]),
          Double.parseDouble(arr[mid][0]));
      long findH3Index = h3.geoToH3(coord.lat, coord.lng, 8);
      if (daejeonH3Index.contains(findH3Index)) {
        l = mid + 1;
      } else {
        r = mid;
      }
    }

    return r;
  }

  //Vincenty's formulae
  public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    final double a = 6378137; // WGS-84 semi-major axis (m)
    final double f = 1 / 298.257223563; // WGS-84 flattening
    final double b = (1 - f) * a; // WGS-84 semi-minor axis (m)

    double L = Math.toRadians(lon2 - lon1);
    double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
    double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
    double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
    double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
    double cosSqAlpha, sinSigma, cos2SigmaM, sigma, sinLambda, cosLambda;
    double lambda = L, lambdaP, iterLimit = 100;
    double cosSigma; // cosSigma 변수 선언

    do {
      sinLambda = Math.sin(lambda);
      cosLambda = Math.cos(lambda);
      sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) +
          (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) *
              (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
      if (sinSigma == 0) {
        return 0;  // coincident points
      }
      cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda; // cosSigma 계산
      sigma = Math.atan2(sinSigma, cosSigma);
      double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
      cosSqAlpha = 1 - sinAlpha * sinAlpha;
      cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
      double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
      lambdaP = lambda;
      lambda = L + (1 - C) * f * sinAlpha *
          (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
    } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

    if (iterLimit == 0) {
      throw new IllegalStateException("Vincenty formula failed to converge");
    }

    double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
    double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
    double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
    double deltaSigma = B * sinSigma *
        (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) -
            B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) *
                (-3 + 4 * cos2SigmaM * cos2SigmaM)));

    double s = b * A * (sigma - deltaSigma);

    return Precision.round(s, 3);
  }

  public GetPlaceResponse getPlace(String poiId){
    Place place = placeRepository.findPlaceByPoiId(poiId);

    if (place == null) return null;
//    if (place == null) throw new CustomException(ErrorCode.PLACE_NOT_FOUND);

    return GetPlaceResponse.from(place);
  }
  /**
   * 장애인 시설 카테고리별 불러오기
   * @return
   */
  public List<PlaceListResponse> getPlaceList(GetPlaceListRequestComment comment) {
    List<Place> placeEntityList = placeRepository.findByType(true);
    double lat = Double.parseDouble(comment.getLat());
    double lng = Double.parseDouble(comment.getLng());

    List<PlaceListResponse> placeList = new ArrayList<>();
    for (Place place : placeEntityList) {
      if(calculateDistance(lat,lng,Double.parseDouble(place.getLatitude()),Double.parseDouble(place.getLongitude()))<=3000)
        placeList.add(PlaceListResponse.from(place));
    }


    return placeList;
  }

  public static List<PathGeoCode> setWheelGeoCode(
      List<PathGeoCode> origin, WheelPathForm wheel, int idx) {
    if (wheel == null) {
      origin.add(idx, null);
      return origin;
    }
    origin.add(idx, PathGeoCode.builder()
        .geoCode(wheel.getGeoCode())
        .trafficType(3)
        .build());
    return origin;
  }

  public List<PathGeoCode> calculateAngle(AngleSlopeCommand command) {
    List<PathGeoCode> pathGeoCodes = command.getGeoCodes();
    log.info("pathGeoCode = {}", pathGeoCodes);
    for(PathGeoCode pathGeoCode : pathGeoCodes){
      if(pathGeoCode == null)
        continue;
      if(pathGeoCode.getTrafficType() == 3){
        ElevationForPathCommand elevation = ElevationForPathCommand.createElevateCommand(pathGeoCode.getGeoCode());
        pathGeoCode.setGeoCode(googleApiService.elevationForPath(elevation).getGeoCode());
      }
    }
    return pathGeoCodes;
  }

  public WheelPathForm useWheelPath(SearchPathToWheelCommand command) {
    WheelPathForm wheelPathForm = tMapApiService.searchPathToWheel(command);
    ElevationForPathCommand elevation = ElevationForPathCommand.createElevateCommand(wheelPathForm.getGeoCode());
    wheelPathForm.setGeoCode(googleApiService.elevationForPath(elevation).getGeoCode());
    return wheelPathForm;
  }
}