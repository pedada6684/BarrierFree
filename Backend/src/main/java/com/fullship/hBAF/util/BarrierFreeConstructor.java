package com.fullship.hBAF.util;

import com.fullship.hBAF.domain.place.entity.Place;
import com.fullship.hBAF.domain.place.repository.PlaceRepository;
import com.fullship.hBAF.domain.place.service.PlaceService;
import com.fullship.hBAF.domain.place.service.command.CreatePlaceCommand;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class BarrierFreeConstructor {

    private final PlaceService placeService;

    @Value("${api.data.license.key}")
    private String serviceKey;
    @Value("${api.tmap.key}")
    private String tmapAppkey;

    private final PlaceRepository placeRepository;
    private ArrayList<HashMap<String, String>> etcData = new ArrayList<>();

    public void saveBarrierFree() throws IOException, ParseException, ParserConfigurationException, SAXException {

        // 공공데이터 포털 배리어프리 장소 저장
        Map<String, String> categoryMap = getCategoryMap();
        for (String faclTyCd : categoryMap.keySet()) {
            String category = categoryMap.get(faclTyCd);

            UriComponents publicDataPlaceUri = UriComponentsBuilder
                    .fromHttpUrl("https://www.bokjiro.go.kr/ssis-tbu/getDisConvFaclList.do")
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("numOfRows", 1000)
                    .queryParam("siDoNm", "대전")
                    .queryParam("faclTyCd", faclTyCd) /*장애인편의시설용도구분코드코드표 참조*/
                    .queryParam("SG_APIM", "2ug8Dm9qNBfD32JLZGPN64f3EoTlkpD8kSOHWfXpyrY")
                    .build();

            RestTemplate rt = new RestTemplate();
            HttpEntity<?> he = new HttpEntity<>(setHttpHeaders());
            ResponseEntity<String> resultMap = rt.exchange(publicDataPlaceUri.toUri(), HttpMethod.GET, he, String.class);

            InputStream is = new ByteArrayInputStream(Objects.requireNonNull(resultMap.getBody()).getBytes("UTF-8"));

            // Document 객체 생성
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            // facInfoList 태그 내의 servList 태그들을 가져오기
            NodeList servList = doc.getElementsByTagName("servList");

            // servList 태그들을 순회하며 각 태그에 해당하는 값들 저장
            for (int idx = 0; idx < servList.getLength(); idx++) {
                Element servElement = (Element) servList.item(idx);

                Element faclNmElement = (Element) servElement.getElementsByTagName("faclNm").item(0);
                String faclNm = (faclNmElement != null) ? faclNmElement.getTextContent() : "";

                Element wfcltIdElement = (Element) servElement.getElementsByTagName("wfcltId").item(0);
                String wfcltId = (wfcltIdElement != null) ? wfcltIdElement.getTextContent() : "";

                Element faclLatElement = (Element) servElement.getElementsByTagName("faclLat").item(0);
                String faclLat =  (faclLatElement != null) ? faclLatElement.getTextContent() : "";

                Element faclLngElement = (Element) servElement.getElementsByTagName("faclLng").item(0);
                String faclLng = (faclLngElement != null) ? faclLngElement.getTextContent() : "";


                // 배리어프리 장소 tmap api로 값 가져오기
                String searchKeyword = faclNm;        //장애시설 db로 장소리스트 받아옴
                if (searchKeyword == null || searchKeyword.equals(" ")) continue;
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
                        .queryParam("centerLon", faclLng)
                        .queryParam("centerLat", faclLat)
                        .queryParam("appKey", tmapAppkey)
                        .build();

                ResponseEntity<String> responseEntity =
                        restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class);
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    //성공

                    JSONParser parser = new JSONParser();
                    try {
                        JSONObject object = (JSONObject) parser.parse(responseEntity.getBody());
                        JSONObject searchPoiInfo = (JSONObject) object.get("searchPoiInfo");
                        JSONObject pois = (JSONObject) searchPoiInfo.get("pois");
                        JSONArray poiArr = (JSONArray) pois.get("poi");

                        for (int i = 0; i < poiArr.size(); i++) {
                            JSONObject poi = (JSONObject) poiArr.get(i);
                            JSONObject newAddressList = (JSONObject) poi.get("newAddressList");
                            JSONArray newAddress = (JSONArray) newAddressList.get("newAddress");
                            if (newAddress == null || newAddress.isEmpty()) continue;
                            JSONObject address = (JSONObject) newAddress.get(0);
                            String fullAddressRoad = (String) address.get("fullAddressRoad");

                            String name = (String) poi.get("name");
                            String frontLat = (String) poi.get("frontLat");
                            String frontLon = (String) poi.get("frontLon");
                            String id = (String) poi.get("id");

                            //위, 경도가 있을 때
                            if (!faclLat.isEmpty() && !faclLng.isEmpty()) {
                                String subFaclLat = faclLat.substring(0, 5);
                                String subFaclLng = faclLng.substring(0, 5);
                                String subFrontLat = frontLat.substring(0, 5);
                                String subFrontLon = frontLon.substring(0, 5);
                                // 위, 경도, 시설명 첫자리 일치 여부 확인
                                if (subFaclLat.equals(subFrontLat) && subFaclLng.equals(subFrontLon) && faclNm.substring(0, 1).equals(name.substring(0, 1))) {
                                    CreatePlaceCommand command = CreatePlaceCommand.builder()
                                            .placeName(name)
                                            .address(fullAddressRoad)
                                            .latitude(frontLat)
                                            .longitude(frontLon)
                                            .poiId(id)
                                            .category(category)
                                            .type(true)
//                                            .barrierFree(barrierfreeInfo)
                                            .wtcltId(wfcltId)
                                            .build();
                                    placeService.createPlace(command);
                                    break;
                                }
                            } else if (name.equals(faclNm)) { //위, 경도 없을 때, 이름 일치만 체크
                                CreatePlaceCommand command = CreatePlaceCommand.builder()
                                        .placeName(name)
                                        .address(fullAddressRoad)
                                        .latitude(frontLat)
                                        .longitude(frontLon)
                                        .poiId(id)
                                        .category(category)
                                        .type(true)
//                                        .barrierFree(barrierfreeInfo)
                                        .wtcltId(wfcltId)
                                        .build();
                                placeService.createPlace(command);
                                break;
                            }
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

//            setBarrierfreeInfo();
        }
    }

    public void setBarrierfreeInfo() throws ParserConfigurationException, IOException, SAXException {
        List<String> WtcltIdList = placeRepository.findWtcltIdByType();

        for (String wtcltId : WtcltIdList) {
            UriComponents publicDataDetailPlaceUri = UriComponentsBuilder
                    .fromHttpUrl("https://www.bokjiro.go.kr/ssis-tbu/getFacInfoOpenApiJpEvalInfoList.do")
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("wfcltId", wtcltId) /*장애인편의시설용도구분코드코드표 참조*/
//                    .queryParam("wfcltId", place.getWtcltId()) /*장애인편의시설용도구분코드코드표 참조*/
                    .queryParam("SG_APIM", "2ug8Dm9qNBfD32JLZGPN64f3EoTlkpD8kSOHWfXpyrY")
                    .build();

            RestTemplate rt = new RestTemplate();
            HttpEntity<?> he = new HttpEntity<>(setHttpHeaders());
            System.out.println(publicDataDetailPlaceUri.toUri());
            ResponseEntity<String> resultMap = rt.exchange(publicDataDetailPlaceUri.toUri(), HttpMethod.GET, he, String.class);

            InputStream is = new ByteArrayInputStream(Objects.requireNonNull(resultMap.getBody()).getBytes("UTF-8"));

            // Document 객체 생성
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            // facInfoList 태그 내의 servList 태그들을 가져오기
            NodeList servList = doc.getElementsByTagName("servList");

            StringBuilder barrierfreeResult = new StringBuilder();

            String evalInfo = "";
            // servList 태그들을 순회하며 각 태그에 해당하는 값들 저장
            for (int idx = 0; idx < servList.getLength(); idx++) {
                Element servElement = (Element) servList.item(idx);

                /**
                 * 	계단 또는 승강설비,대변기,복도,소변기,일반사항,장애인전용주차구역,주출입구 높이차이 제거,주출입구 접근로,출입구(문),해당시설 층수
                 * 	대변기, 소변기 -> 변기 a, 장애인전용주차구역 -> 주차 b, 주출입구 높이차이 제거 -> 높이 c, 주출입구 접근로 -> 접근로 d, 출입구(문) -> 문 e,승강설비 -> 승강 f, 나머지는 이어 붙이기
                 */
                Element evalInfoElement = (Element) servElement.getElementsByTagName("evalInfo").item(0);
                evalInfo = (evalInfoElement != null) ? evalInfoElement.getTextContent() : "";

                String[] inputs = evalInfo.split(",");
                for (String input : inputs) {
                    if (input.contains("변기")) barrierfreeResult.append("a");
                    else if (input.contains("주차")) barrierfreeResult.append("b");
                    else if (input.contains("높이")) barrierfreeResult.append("c");
                    else if (input.contains("접근로")) barrierfreeResult.append("d");
                    else if (input.contains("문")) barrierfreeResult.append("e");
                    else if (input.contains("승강")) barrierfreeResult.append("f");
                    else {
                        HashMap<String, String> etc = new HashMap<>();
                        etc.put(wtcltId, input);
                        etcData.add(etc);
                    }
                }
            }

            char[] beforeSort = barrierfreeResult.toString().toCharArray();
            Arrays.sort(beforeSort);

            Place place = placeRepository.findByWtcltId(wtcltId)
                    .orElseThrow(() -> new IllegalArgumentException("NO CONTENT wtcltId " + wtcltId));
            place.updateBarrierFree(new String(beforeSort));
        }

//        System.out.println("------------------------우리에게 없는 배리어프리 기구 출력------------------------------");
//        for (Map<String, String> data : etcData) {
//            System.out.println("wfcid: " + data.keySet() + ", data: " + data.values());
//        }
    }

    private Map<String, String> getCategoryMap() {
        Map<String, String> categoryMap = new HashMap<>();

        categoryMap.put("UC0A13", "화장실"); //2개

//        categoryMap.put("UC0B01", "음식점"); //95개
        categoryMap.put("UC0B02", "음식점"); //1개

        categoryMap.put("UC0F01", "병원");
        categoryMap.put("UC0F03", "병원");
        categoryMap.put("UC0F02", "병원");
        categoryMap.put("UC0A14", "병원");
        categoryMap.put("UC0A06", "병원");

        categoryMap.put("UC0J01", "문화");
        categoryMap.put("UC0C02", "문화");
        categoryMap.put("UC0G09", "문화");
        categoryMap.put("UC0T02", "문화");
        categoryMap.put("UC0C03", "문화");
        categoryMap.put("UC0T01", "문화");
        categoryMap.put("UC0A07", "문화");
        categoryMap.put("UC0C01", "문화");
        categoryMap.put("UC0C04", "문화");
        categoryMap.put("UC0C05", "문화");
        categoryMap.put("UC0R01", "문화");
        categoryMap.put("UC0J02", "문화");

        categoryMap.put("UC0A05", "편의");
        categoryMap.put("UC0A01", "편의");
        categoryMap.put("UC0N01", "편의");
        categoryMap.put("UC0R02", "편의");
        categoryMap.put("UC0E01", "편의");

        categoryMap.put("UC0L01", "숙박");
        categoryMap.put("UC0L02", "숙박");
        return categoryMap;
    }

    private HttpHeaders setHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}