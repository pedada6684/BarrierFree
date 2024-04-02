package com.fullship.hBAF.util;

import com.fullship.hBAF.domain.place.entity.Place;
import com.fullship.hBAF.domain.place.repository.PlaceRepository;
import com.fullship.hBAF.domain.place.service.PlaceService;
import com.fullship.hBAF.domain.place.service.command.CreatePlaceCommand;
import com.fullship.hBAF.global.api.response.KakaoPlace;
import com.fullship.hBAF.global.api.service.KakaoMapApiService;
import com.fullship.hBAF.global.api.service.command.SearchKakaoPlaceCommand;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
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
import java.io.*;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class BarrierFreeConstructor {

    private final PlaceService placeService;
    private final KakaoMapApiService kakaoMapApiService;

    @Value("${api.data.license.key}")
    private String serviceKey;

    private final PlaceRepository placeRepository;
    private final ResourceLoader resourceLoader;
    private final ImageCrawler imageCrawler;
    private ArrayList<HashMap<String, String>> etcData = new ArrayList<>();

    /**
     * 베리어프리 장소 저장 메서드
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public void saveBarrierFree() throws IOException, ParserConfigurationException, SAXException {
        List<Map<String, String>> places = searchBarrierFreePlace();
//        saveBarrierFreePlace(places);
//        setBarrierfreeInfo();
//        addBFPlaceInBuilding();
//        setBarrierfreeThumbnail();
//        setPlaceDetail();
    }

    /**
     * DB에 저장된 place의 핸드폰 번호와 url을 update하는 메서드
     */
    private void setPlaceDetail() {
        List<Place> allPlace = placeRepository.findAll();
        for (Place place : allPlace) {
            SearchKakaoPlaceCommand command = SearchKakaoPlaceCommand.builder()
                    .lng(place.getLongitude())
                    .lat(place.getLatitude())
                    .keyword(place.getPlaceName())
                    .build();
            KakaoPlace kakaoPlace = kakaoMapApiService.getKakaoPlace(command);
            if (kakaoPlace == null) continue;
            String phone = kakaoPlace.getPhone() != null ? kakaoPlace.getPhone() : "";
            String placeUrl = kakaoPlace.getPlaceUrl() != null ? kakaoPlace.getPlaceUrl() : "";
            place.updateDetail(phone, placeUrl);
        }
    }

    private void setBarrierfreeThumbnail() {
        List<Place> placeList = placeRepository.findByType(true);
        imageCrawler.updatelBFImage(placeList);
    }

    /**
     * api를 통해서 무장애 place를 받아오는 메서드
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    private List<Map<String, String>> searchBarrierFreePlace() throws IOException, ParserConfigurationException, SAXException {
        // 공공데이터 포털 배리어프리 장소 저장
        List<Map<String, String>> placeInfoList = new ArrayList<>();
        for (String faclTyCd : CATEGORY_MAP.keySet()) {
            String category = CATEGORY_MAP.get(faclTyCd);

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

                if (faclLat == null || faclLng == null) continue;
                Map<String, String> placeInfo = new HashMap<>();
                placeInfo.put("searchKeyword", searchKeyword);
                placeInfo.put("faclLat", faclLat);
                placeInfo.put("faclLng", faclLng);
                placeInfo.put("wfcltId", wfcltId);
                placeInfo.put("category", category);

                placeInfoList.add(placeInfo);
            }
        }
        return placeInfoList;
    }

    /**
     * 카카오맵에서 검증 이후 DB에 저장하는 메서드
     * @param placeInfoList: 저장할 placeList
     */
    private void saveBarrierFreePlace(List<Map<String, String>> placeInfoList) {
        for (Map<String, String> placeInfo: placeInfoList) {
            SearchKakaoPlaceCommand searchCommand = SearchKakaoPlaceCommand.builder()
                    .lat(placeInfo.get("faclLng"))
                    .lng(placeInfo.get("faclLat"))
                    .keyword(placeInfo.get("searchKeyword"))
                    .build();

            //카카오 검색 (검증)
            KakaoPlace kakaoPlace = kakaoMapApiService.getKakaoPlace(searchCommand);
            if (kakaoPlace == null) continue;

            //db저장
            CreatePlaceCommand createCommand = CreatePlaceCommand.fromKakaoPlace(kakaoPlace);
            createCommand.setCategory(placeInfo.get("category"));
            createCommand.setWtcltId(placeInfo.get("wfcltId"));
            placeService.createPlace(createCommand);
        }
    }

    /**
     * DB에 존재하는 빌딩, 타워 내 점포를 DB에 저장하는 메서드
     */
    public void addBFPlaceInBuilding() {
        String[] BuildingOrTower = {"빌딩", "타워"};
        for (String keyword : BuildingOrTower) {
            //DB에서 빌딩 검색
            List<Place> buildings = placeRepository.findPlacesByTypeTrueAndPlaceNameContaining(keyword);
            for (Place building : buildings) {
                if (building.getAddress().isEmpty()) continue; //주소 없는 빌딩 생략
                //카카오맵 검색
                SearchKakaoPlaceCommand command = SearchKakaoPlaceCommand.builder()
                        .keyword(building.getAddress())
                        .lat(building.getLatitude())
                        .lng(building.getLongitude())
                        .build();
                List<KakaoPlace> kakaoPlaces = kakaoMapApiService.searchKakaoPlace(command);
                for (KakaoPlace kakaoPlace : kakaoPlaces) {
                    //빌딩 주소와 카카오 주소가 동일할때만 작동
                    if (!kakaoPlace.getFullAddressRoad().equals(building.getAddress())) continue;

                    CreatePlaceCommand createPlaceCommand = CreatePlaceCommand.builder()
                            .placeName(kakaoPlace.getName())
                            .address(kakaoPlace.getFullAddressRoad())
                            .latitude(kakaoPlace.getFrontLat())
                            .longitude(kakaoPlace.getFrontLon())
                            .poiId(kakaoPlace.getId())
                            .category(kakaoPlace.getCategory())
                            .barrierFree(building.getBarrierFree())
                            .phone(kakaoPlace.getPhone())
                            .placeUrl(kakaoPlace.getPlaceUrl())
                            .type(true)
                            .build();
                    placeService.createPlace(createPlaceCommand);
                }
            }
        }
    }

    public void setBarrierfreeInfo() throws ParserConfigurationException, IOException, SAXException {
        List<String> WtcltIdList = placeRepository.findWtcltIdByType();

        int cnt = 0;
        for (String wtcltId : WtcltIdList) {
            if (wtcltId == null) continue;

            if (cnt++ == 90) break;

            UriComponents publicDataDetailPlaceUri = UriComponentsBuilder
                    .fromHttpUrl("https://www.bokjiro.go.kr/ssis-tbu/getFacInfoOpenApiJpEvalInfoList.do")
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("wfcltId", wtcltId) /*장애인편의시설용도구분코드코드표 참조*/
                    .queryParam("SG_APIM", "2ug8Dm9qNBfD32JLZGPN64f3EoTlkpD8kSOHWfXpyrY")
                    .build();

            RestTemplate rt = new RestTemplate();
            HttpEntity<?> he = new HttpEntity<>(setHttpHeaders());
//            System.out.println(publicDataDetailPlaceUri.toUri());
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

    }

    private HttpHeaders setHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private Workbook getExcelSheets() throws IOException {
//        Resource resource = resourceLoader.getResource("classpath:data/wheelchair_test.xls");
//        InputStream inputStream = resource.getInputStream();
        Resource resource = new ClassPathResource("data/wheelchair.xls");
        String filePath = resource.getFile().getAbsolutePath();
//        String filePath = "C:/Users/SYJ/PJT/wheelchair_test.xls";

        String fileExtsn = FilenameUtils.getExtension(
                filePath.substring(26)); // 파일 Original 이름 불러오기 ex) 전문가.xlsx

        Workbook workbook = null;
        try {
            // 엑셀 97 - 2003 까지는 HSSF(xls),  엑셀 2007 이상은 XSSF(xlsx)
            if (fileExtsn.equals("xls")) {
                workbook = new HSSFWorkbook(new FileInputStream(filePath));
            } else {
                workbook = new XSSFWorkbook(new FileInputStream(filePath));
            }
        } catch (IOException e) {
            throw new CustomException(ErrorCode.ABNORMAL_FILE_READ);
        }
        return workbook;
    }

    public void saveElectricWheelchairExcel() throws IOException {
        Sheet sheet = getExcelSheets().getSheetAt(0);// 첫 번째 시트만 읽기
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String searchKeyword = row.getCell(0).getStringCellValue();
            String faclLat = String.valueOf(row.getCell(1).getNumericCellValue());
            String faclLng = String.valueOf(row.getCell(2).getNumericCellValue());
            System.out.println(searchKeyword + ", " + faclLat + ", " + faclLng);

            SearchKakaoPlaceCommand searchCommand = SearchKakaoPlaceCommand.builder()
                    .lat(faclLat)
                    .lng(faclLng)
                    .keyword(searchKeyword)
                    .category("휠체어 충전소")
                    .build();
            KakaoPlace kakaoPlace = kakaoMapApiService.getKakaoPlace(searchCommand);
            CreatePlaceCommand createPlaceCommand = CreatePlaceCommand.fromKakaoPlace(kakaoPlace);
            createPlaceCommand.setCategory("휠체어 충전소");
            placeService.createPlace(createPlaceCommand);
        }
    }

    private static final Map<String, String> CATEGORY_MAP = new HashMap<>();

    static {
        CATEGORY_MAP.put("UC0A13", "화장실");
        CATEGORY_MAP.put("UC0B01", "음식점");
        CATEGORY_MAP.put("UC0B02", "음식점");
        CATEGORY_MAP.put("UC0F01", "의료");
        CATEGORY_MAP.put("UC0F03", "의료");
        CATEGORY_MAP.put("UC0F02", "의료");
        CATEGORY_MAP.put("UC0A14", "의료");
        CATEGORY_MAP.put("UC0A06", "의료");
        CATEGORY_MAP.put("UC0J01", "문화");
        CATEGORY_MAP.put("UC0C02", "문화");
        CATEGORY_MAP.put("UC0G09", "문화");
        CATEGORY_MAP.put("UC0T02", "문화");
        CATEGORY_MAP.put("UC0C03", "문화");
        CATEGORY_MAP.put("UC0T01", "문화");
        CATEGORY_MAP.put("UC0A07", "문화");
        CATEGORY_MAP.put("UC0C01", "문화");
        CATEGORY_MAP.put("UC0C04", "문화");
        CATEGORY_MAP.put("UC0C05", "문화");
        CATEGORY_MAP.put("UC0R01", "문화");
        CATEGORY_MAP.put("UC0J02", "문화");
        CATEGORY_MAP.put("UC0A05", "편의");
        CATEGORY_MAP.put("UC0A01", "편의");
        CATEGORY_MAP.put("UC0N01", "편의");
        CATEGORY_MAP.put("UC0R02", "편의");
        CATEGORY_MAP.put("UC0E01", "편의");
        CATEGORY_MAP.put("UC0L01", "숙박");
        CATEGORY_MAP.put("UC0L02", "숙박");
    }
}