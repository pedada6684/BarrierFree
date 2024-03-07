package com.fullship.hBAF.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fullship.hBAF.domain.place.service.PlaceService;
import com.fullship.hBAF.global.api.service.DataApiService;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class BarrierFreeConstructor {

    private final PlaceService placeService;
    private final DataApiService dataApiService;

    @Value("${tmap.poi.appkey}")
    private String tmapAppkey;
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void saveBarrierFree() throws UnsupportedEncodingException, ParseException, JsonProcessingException{
        String searchKeyword = "성심당";        //장애시설 db로 장소리스트 받아옴
        //장애시설의 이름을 tmap에 검색하여 실제로 존재하는 장소인지 검증
        HttpHeaders headers = setHttpHeaders();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        UriComponents uri = UriComponentsBuilder
                .fromHttpUrl("https://apis.openapi.sk.com/tmap/pois")
                .queryParam("version", 1)
                .queryParam("searchKeyword", searchKeyword)
                .queryParam("count", 10)
                .queryParam("resCoordType", "WGS84GEO")
                .queryParam("reqCoordType", "WGS84GEO")
                .queryParam("areaLLCode", 30)
                .queryParam("areaLMCode", 140)
                .queryParam("searchtypCd", "A")
                .queryParam("radius", 30)
                .queryParam("centerLon", 127.38484007312708 )
                .queryParam("centerLat", 36.350469041429)
                .queryParam("appKey", tmapAppkey)
                .build();

        ResponseEntity<String> responseEntity =
                restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            //성공
            System.out.println("responseEntity.getBody()");
            System.out.println(responseEntity.getBody());

            JSONParser parser = new JSONParser();
            try {
                JSONObject object = (JSONObject) parser.parse(responseEntity.getBody());
                JSONObject searchPoiInfo = (JSONObject) object.get("searchPoiInfo");
                JSONObject pois = (JSONObject) searchPoiInfo.get("pois");
                JSONArray poiArr = (JSONArray) pois.get("poi");


                for (int i = 0; i < poiArr.size(); i++) {

                    JSONObject poi = (JSONObject) poiArr.get(i);
                    JSONObject newAddressList = (JSONObject) poi.get("newAddressList");
                    JSONArray newAddress = (JSONArray)newAddressList.get("newAddress");
                    JSONObject address = (JSONObject) newAddress.get(0);
                    String fullAddressRoad = (String) address.get("fullAddressRoad");

                    String name = (String) poi.get("name");
                    String frontLat = (String) poi.get("frontLat");
                    String frontLon = (String) poi.get("frontLon");
                    String id = (String) poi.get("id");

                    CreatePlaceCommand command = CreatePlaceCommand.builder()
                            .placeName(name)
                            .address(fullAddressRoad)
                            .latitude(frontLat)
                            .longitude(frontLon)
                            .poiId(id)
//                            .category()
                            .barrierFree(true)
//                            .wtcltId()
                            .build();
                    placeService.createPlace(command);
                }

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            log.error(responseEntity.getStatusCode().toString() + " : "+searchKeyword);
        } else {
            throw new CustomException(ErrorCode.NO_AVAILABLE_API);
        }
    }

    //tmap에서 받은 정보를 mysql에 저장

    private HttpHeaders setHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    @Order(1)
    public void saveBusInfo() {
        dataApiService.saveBusInfo();
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    @Order(2)
    public void saveBusRouteInfo() {
        dataApiService.saveRoute();
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    @Order(3)
    public void saveBusStop(){

        dataApiService.saveBusStop();
    }

  @EventListener(ApplicationReadyEvent.class)
  @Transactional
  @Order(4)
  public void saveSubway(){

    dataApiService.saveSubway();
  }

}