package com.fullship.hBAF.domain.place.service;

import com.fullship.hBAF.domain.place.controller.response.PlaceListResonse;
import com.fullship.hBAF.domain.place.entity.Image;
import com.fullship.hBAF.domain.place.entity.Place;
import com.fullship.hBAF.domain.place.repository.ImageRepository;
import com.fullship.hBAF.domain.place.repository.PlaceRepository;
import com.fullship.hBAF.domain.place.service.command.CreatePlaceCommand;
import com.fullship.hBAF.domain.place.service.command.UpdatePlaceImageCommand;
import com.fullship.hBAF.global.api.response.TransitPathForm;
import com.fullship.hBAF.global.api.service.TMapApiService;
import com.fullship.hBAF.global.api.service.command.SearchPathToTransitCommand;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import com.uber.h3core.H3Core;
import com.uber.h3core.util.GeoCoord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.fullship.hBAF.util.H3.daejeonH3Index;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

  private final PlaceRepository placeRepository;
  private final TMapApiService tMapApiService;
  private final ImageRepository imageRepository;

  public String useTransitPath(SearchPathToTransitCommand command) throws ParseException {
    List<TransitPathForm> list = tMapApiService.searchPathToTransit(command);

    List<TransitPathForm> transferList = new ArrayList<>(list);
    Collections.sort(transferList,
        (o1, o2) -> (int) (o1.getTransferCount() - o2.getTransferCount()));

    int idx = 0;
    long min = transferList.get(0).getTransferCount();
    for (int i = 0; i < transferList.size(); i++, idx++) {
      if(transferList.get(i).getTransferCount() > min + 1)
        break;
    }

    for(int i = idx; i < transferList.size(); i++){
      transferList.remove(idx);
    }
      return null;
  }

    @Transactional(readOnly = false)
    public Long createPlace(CreatePlaceCommand command){
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
  public Long updatePlaceImageUrl(UpdatePlaceImageCommand command){
    Place place = placeRepository.findById(command.getPlaceId()).orElseThrow(() -> new IllegalStateException());
    Optional<Image> imageOptional = imageRepository.findByPlaceAndImageType(place, 0);
    if (imageOptional.isPresent()){
      Image image = imageOptional.get();
      image.updateImageUrl(command.getImageUrl());
    }else { // 썸네일 이미지 없는 생성
      Image newImage = Image.createNewImage(place, command.getImageUrl(), 0);
      imageRepository.save(newImage);
    }
    return place.getId();
  }

  public Double calculateCost(String[][] arr) throws IOException {
    H3Core h3 = H3Core.newInstance();
    GeoCoord coord = new GeoCoord(Double.parseDouble(arr[arr.length - 1][1]), Double.parseDouble(arr[arr.length - 1][0]));
    long lastH3Index = h3.geoToH3(coord.lat, coord.lng, 8);

    int clockIdx = arr.length-1;
    if (!daejeonH3Index.contains(lastH3Index))
      clockIdx = findClock(arr);

    int cost = 1000;
    double totalDis = 0;
    double totalCostDis = -3000;
    double totalClockDis = 0;
    double preLat = Double.parseDouble(arr[0][1]);
    double preLng = Double.parseDouble(arr[0][0]);
    for(int i = 1; i<arr.length; i++){
      double curLat = Double.parseDouble(arr[i][1]);
      double curLng = Double.parseDouble(arr[i][0]);

      double dis = calculateDistance(preLat,preLng,curLat,curLng);
      totalDis += dis;
      if(i>clockIdx) {
        totalCostDis += dis * 1.2;
        totalClockDis += dis;
      }
      else
        totalCostDis += dis;

      preLat = curLat;
      preLng = curLng;
    }
    totalCostDis = Math.max(0,totalCostDis);
    System.out.println("토탈 거리 : "+totalDis);
    System.out.println("시계 위치 : "+arr[clockIdx][1]+" "+arr[clockIdx][0]);
    System.out.println("시계 거리 : "+totalClockDis);
    System.out.println("금액 : "+(cost + totalCostDis*100/440));
    return (cost + totalCostDis*100/440);
  }

  public int findClock(String[][] arr) throws IOException {
    int l = 0;
    int r = arr.length-1;

    H3Core h3 = H3Core.newInstance();

    while(l<r){
      int mid = (l+r) >>> 1;

      GeoCoord coord = new GeoCoord(Double.parseDouble(arr[mid][1]),Double.parseDouble(arr[mid][0]));
      long findH3Index = h3.geoToH3(coord.lat, coord.lng, 8);
      if(daejeonH3Index.contains(findH3Index))
        l = mid+1;
      else
        r = mid;
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
      if (sinSigma == 0) return 0;  // coincident points
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
  /**
   * 장애인 시설 카테고리별 불러오기
   * @param category
   * @return
   */
  public List<PlaceListResonse> getPlaceByCategory(String category) {
    List<Place> placeEntityList = placeRepository.findByCategory(category);

    List<PlaceListResonse> placeList = new ArrayList<>();
    for (Place place : placeEntityList) {
      placeList.add(PlaceListResonse.from(place));
    }

    return placeList;
  }
}