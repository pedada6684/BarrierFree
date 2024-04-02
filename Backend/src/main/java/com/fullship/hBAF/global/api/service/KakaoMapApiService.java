package com.fullship.hBAF.global.api.service;

import com.fullship.hBAF.global.api.response.KakaoPlace;
import com.fullship.hBAF.global.api.service.command.searchBFCommand;
import com.fullship.hBAF.global.api.service.command.SearchKakaoPlaceCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoMapApiService {

  @Value("${api.kakaoMap.key}")
  private String appkey;
  private final String kakaoUrl = "https://dapi.kakao.com/v2/local/search/keyword.json";

  private final ApiService<String> apiService;

  private HttpHeaders setHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "KakaoAK " + appkey);
    return headers;
  }

  /**
   * 카카오맵 검색을 통하여 검색조건에 맞는 하나의 장소를 리턴하는 메서드
   * @param command 위도, 경도, 검색어, 카테고리
   * @return kakaoPlace
   */
  public KakaoPlace getKakaoPlace(SearchKakaoPlaceCommand command) {
    String searchKeywordDW = StringUtils.deleteWhitespace(command.getKeyword());
    List<KakaoPlace> kakaoPlaces = searchKakaoPlace(command);
    //일치하는지 검사하는 코드
    //일치하는 하나의 장소만 리턴
    for (KakaoPlace kaKaoPlace : kakaoPlaces) {
      String kakaoPlaceNameDW = StringUtils.deleteWhitespace(kaKaoPlace.getName());
      if (kakaoPlaceNameDW.contains(searchKeywordDW)) { //검색결과 일치 검사
        return kaKaoPlace;
      }
    }
    return null;
  }

  /**
   *
   * @param command
   * @return
   */
  public KakaoPlace searchBF(searchBFCommand command) {
    String searchKeywordDW = StringUtils.deleteWhitespace(command.getKeyword());
    List<KakaoPlace> kakaoPlaces = searchKakaoPlace(command.convertToSearchCommand());
    //일치하는지 검사하는 코드
    //일치하는 하나의 장소만 리턴
    for (KakaoPlace kaKaoPlace : kakaoPlaces) {
      String kakaoPlaceNameDW = StringUtils.deleteWhitespace(kaKaoPlace.getName());
      if (kakaoPlaceNameDW.contains(searchKeywordDW)) { //검색결과 일치 검사
        return kaKaoPlace;
      }
    }
    return null;
  }

  /**
   * 검색어 기반 100미터 이내 검색 메서드
   * @param command 위도, 경도, 장소이름
   * @return 검색장소 기반 100이내 장소 리스트 (KakaoPlace)
   */
  public List<KakaoPlace> searchKakaoPlace(SearchKakaoPlaceCommand command) {
      UriComponents uri = UriComponentsBuilder
                .fromHttpUrl(kakaoUrl)
                .queryParam("x", command.getLat())
                .queryParam("y", command.getLng())
                .queryParam("radius", "300")
                .queryParam("query", command.getKeyword())
                .encode()
                .build();
    ResponseEntity<String> responseEntity =
            apiService.get(uri.toUri(), setHttpHeaders(), String.class);
    return KakaoPlace.jsonToO(responseEntity);
  }
}