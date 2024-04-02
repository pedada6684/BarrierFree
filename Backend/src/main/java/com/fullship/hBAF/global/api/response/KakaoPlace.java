package com.fullship.hBAF.global.api.response;

import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
@Builder
public class KakaoPlace {

  private String name;
  private String fullAddressRoad;
  private String frontLat;
  private String frontLon;
  private String id;
  private String category;
  private String phone;
  private String placeUrl;

  public static List<KakaoPlace> jsonToO(ResponseEntity<String> result) {
//    log.info("result = {}", result.getBody());
    List<KakaoPlace> kakaoPlaces = new ArrayList<>();
    try {
      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(result.getBody());
      JSONArray documents = (JSONArray) object.get("documents");

      for (int i = 0; i < documents.size(); i++) {
        JSONObject document = (JSONObject) documents.get(i);
        String placeName = (String) document.get("place_name");
        String roadAddressName = (String) document.get("road_address_name");
        String id = (String) document.get("id");
        String x = (String) document.get("x");
        String y = (String) document.get("y");
        String phone = (String) document.get("phone");
        String placeUrl = (String) document.get("place_url");


        String categoryName = (String) document.get("category_name");
        String category = categoryName.split(">")[0].trim();
        String convertedCategory = CATEGORY_MAP.get(category);

        KakaoPlace kaKaoPlace = KakaoPlace.builder()
                .id(id)
                .name(placeName)
                .frontLon(x)
                .frontLat(y)
                .category(convertedCategory)
                .fullAddressRoad(roadAddressName)
                .phone(phone)
                .placeUrl(placeUrl)
                .build();
        kakaoPlaces.add(kaKaoPlace);
      }
      return kakaoPlaces;
    } catch (ParseException p) {
      log.error("Parsing 실패");
      throw new CustomException(ErrorCode.JSON_PARSE_IMPOSSIBLE);
    }
  }
  private static final Map<String, String> CATEGORY_MAP = new HashMap<>();
  static {
    CATEGORY_MAP.put("사회,공공기관", "기타");
    CATEGORY_MAP.put("가정,생활", "편의");
    CATEGORY_MAP.put("서비스,산업", "기타");
    CATEGORY_MAP.put("의료,건강", "의료");
    CATEGORY_MAP.put("이슈", "의료");
    CATEGORY_MAP.put("음식점", "음식점");
    CATEGORY_MAP.put("교육,학문", "교육");
    CATEGORY_MAP.put("금융,보험", "금융");
    CATEGORY_MAP.put("스포츠,레저", "스포츠");
    CATEGORY_MAP.put("문화,예술", "문화");
    CATEGORY_MAP.put("부동산", "기타");
    CATEGORY_MAP.put("교통,수송", "교통");
    CATEGORY_MAP.put("언론,미디어", "기타");
    CATEGORY_MAP.put("여행", "숙박");
  }
}
