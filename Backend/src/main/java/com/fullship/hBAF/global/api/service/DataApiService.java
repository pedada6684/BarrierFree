package com.fullship.hBAF.global.api.service;

import com.fullship.hBAF.domain.busInfo.entity.BusInfo;
import com.fullship.hBAF.domain.busInfo.repository.BusInfoRepository;
import com.fullship.hBAF.domain.busRouteInfo.entity.BusRouteInfo;
import com.fullship.hBAF.domain.busRouteInfo.repository.BusRouteInfoRepository;
import com.fullship.hBAF.domain.busStop.entity.BusStop;
import com.fullship.hBAF.domain.busStop.repository.BusStopRepository;
import com.fullship.hBAF.domain.metroInfo.entity.MetroInfo;
import com.fullship.hBAF.domain.metroInfo.repository.MetroInfoRepository;
import com.fullship.hBAF.domain.stationInfo.entity.StationInfo;
import com.fullship.hBAF.domain.stationInfo.repository.StationInfoRepository;
import com.fullship.hBAF.domain.stationStopInfo.entity.StationStopInfo;
import com.fullship.hBAF.domain.stationStopInfo.repository.StationStopInfoRepository;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataApiService {

  private final ApiService<String> apiService;
  private final BusInfoRepository busInfoRepository;
  private final BusStopRepository busStopRepository;
  private final BusRouteInfoRepository busRouteInfoRepository;
  private final MetroInfoRepository metroInfoRepository;
  private final StationInfoRepository stationInfoRepository;
  private final StationStopInfoRepository stationStopInfoRepository;

  @Value("${api.data.license.key}")
  private String dataLicenseKey;

  @Value("${api.data.route.key}")
  private String routeKey;

  @Value(("${api.data.bus.key}"))
  private String odSayKey;

  private HttpHeaders setHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  @Transactional(readOnly = false)
  public void saveBusInfo() {
    try {
      /* 전체 페이지 개수 구하기 위한 API 호출 */
      UriComponents uriComponents = UriComponentsBuilder
          .fromHttpUrl("http://openapitraffic.daejeon.go.kr/api/rest/busreginfo/getBusRegInfoAll")
          .queryParam("serviceKey", dataLicenseKey)
          .queryParam("reqPage", 1)
          .build(true);

      ResponseEntity<String> response = apiService.get(uriComponents.toUri(), setHttpHeaders(),
          String.class);

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      Document document = builder.parse(
          new InputSource(new StringReader(response.getBody())));
      document.getDocumentElement().normalize();
      NodeList itemPageCnt = document.getElementsByTagName("itemPageCnt");

      /* 페이지 변경하며 API 호출 */
      for (int i = 1; i <= Integer.parseInt(itemPageCnt.item(0).getTextContent()); i++) {
        uriComponents = UriComponentsBuilder
            .fromHttpUrl("http://openapitraffic.daejeon.go.kr/api/rest/busreginfo/getBusRegInfoAll")
            .queryParam("serviceKey", dataLicenseKey)
            .queryParam("reqPage", i)
            .build(true);
        response = apiService.get(uriComponents.toUri(), setHttpHeaders(), String.class);

        document = builder.parse(new InputSource(new StringReader(response.getBody())));
        document.getDocumentElement().normalize();
        NodeList busType = document.getElementsByTagName("BUS_TYPE");
        NodeList routeId = document.getElementsByTagName("ROUTE_CD");
        NodeList busRegNo = document.getElementsByTagName("CAR_REG_NO");
        for (int j = 0; j < busType.getLength(); j++) {
          BusInfo busInfoByLicense = busInfoRepository.findBusInfoByBusRegNo(
              busRegNo.item(j).getTextContent());
          if (busInfoByLicense != null) {
            continue;
          }
          BusInfo busInfo =
              BusInfo.createBusInfo(
                  busRegNo.item(j).getTextContent(),
                  busType.item(j).getTextContent(),
                  routeId.item(j).getTextContent());
          busInfoRepository.save(busInfo);
        }
      }
    } catch (ParserConfigurationException | IOException | SAXException e) {
      throw new RuntimeException(e);
    }

  }

  @Transactional(readOnly = false)
  public void saveRoute() {
    try {
      UriComponents uriComponents = UriComponentsBuilder
          .fromHttpUrl("http://apis.data.go.kr/1613000/BusRouteInfoInqireService/getRouteNoList")
          .queryParam("serviceKey", routeKey)
          .queryParam("pageNo", 1)
          .queryParam("numOfRows", 1000)
          .queryParam("_type", "json")
          .queryParam("cityCode", 25)
          .build(true);
      ResponseEntity<String> response = apiService.get(uriComponents.toUri(), setHttpHeaders(),
          String.class);

      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(response.getBody());
      JSONObject res = (JSONObject) object.get("response");
      JSONObject body = (JSONObject) res.get("body");
      JSONObject items = (JSONObject) body.get("items");
      JSONArray arrItems = (JSONArray) items.get("item");

      for (Object arrItem : arrItems) {
        JSONObject item = (JSONObject) arrItem;

        String routeType = (String) item.get("routetp");
        String busNo = item.get("routeno").toString();
        if (routeType.equals("첨단버스") || routeType.equals("급행버스") || routeType.equals("마을버스")) {
          busNo = routeType.substring(0, 2) + busNo;
        }
        BusRouteInfo busRouteInfo = BusRouteInfo.createBusRouteInfo(
            busNo, ((String) item.get("routeid")).substring(3),
            routeType);

        busRouteInfoRepository.save(busRouteInfo);

      }

    } catch (ParseException e) {
      throw new CustomException(ErrorCode.JSON_PARSE_IMPOSSIBLE);

    }

  }

  @Transactional(readOnly = false)
  public void saveDetailRoute() {
    try {
      List<BusRouteInfo> busRouteInfos = busRouteInfoRepository.findAll();
      for (BusRouteInfo busRouteInfo : busRouteInfos) {
        BusRouteInfo info = busRouteInfoRepository.findBusRouteInfoByPublicBusId(
            busRouteInfo.getPublicBusId());
        if (info.getBusId() != null) {
          continue;
        }

        String purpose = busRouteInfo.getPurpose();
        String busNo = busRouteInfo.getBusNo();
        if (purpose.equals("급행버스") || purpose.equals("마을버스")) {
          busNo = busNo.substring(2);
        }

        /* 계룡 버스 확인 */
        int CID = 3000;
        if (info.getPublicBusId().substring(0, 4).equals("6969")) {
          CID = 3010;
        }

        URI uri = UriComponentsBuilder
            .fromHttpUrl("https://api.odsay.com/v1/api/searchBusLane")
            .queryParam("apiKey", URLEncoder.encode(odSayKey, StandardCharsets.UTF_8))
            .queryParam("lang", 0)
            .queryParam("busNo",
                URLEncoder.encode(busNo, StandardCharsets.UTF_8))
            .queryParam("CID", CID)
            .build(true).toUri();

        ResponseEntity<String> response = apiService.get(uri, setHttpHeaders(), String.class);

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(response.getBody());
        JSONObject result = (JSONObject) object.get("result");
        JSONArray lanes = (JSONArray) result.get("lane");
        for (int j = 0; j < lanes.size(); j++) {
          JSONObject lane = (JSONObject) ((JSONArray) result.get("lane")).get(j);
          String lbId = ((String) lane.get("localBusID"));

          if (lbId.equals("0")) {
            continue;
          }

          BusRouteInfo routeInfo = busRouteInfoRepository.findBusRouteInfoByPublicBusId(
              ((String) lane.get("localBusID")).substring(3));

          routeInfo.updateBusRouteInfo(
              lane.get("busID").toString(),
              lane.get("busStartPoint").toString(),
              lane.get("busEndPoint").toString(),
              lane.get("busFirstTime").toString(),
              lane.get("busLastTime").toString());
        }
      }
    } catch (ParseException e) {
      throw new CustomException(ErrorCode.JSON_PARSE_IMPOSSIBLE);
    }
  }

  @Transactional(readOnly = false)
  public void saveStopInfo() {
    List<BusRouteInfo> busRouteInfos = busRouteInfoRepository.findAll();
    for (BusRouteInfo busRouteInfo : busRouteInfos) {
      String busId = busRouteInfo.getBusId();
      String busNo = busRouteInfo.getBusNo();
      URI uri;
      uri = UriComponentsBuilder
          .fromHttpUrl("https://api.odsay.com/v1/api/busLaneDetail")
          .queryParam("apiKey", URLEncoder.encode(odSayKey, StandardCharsets.UTF_8))
          .queryParam("lang", 0)
          .queryParam("busID", busId)
          .build(true).toUri();

      ResponseEntity<String> response = apiService.get(uri, setHttpHeaders(), String.class);
      try {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(response.getBody());
        JSONObject result = (JSONObject) object.get("result");
        JSONArray stations = (JSONArray) result.get("station");

        for (Object o : stations) {
          JSONObject station = (JSONObject) o;
          String arsId = station.get("arsID") == null ? "0" : station.get("arsID").toString();
          BusStop busInfo = BusStop.createBusInfo(
              busId, busNo,
              station.get("stationID").toString(),
              station.get("stationName").toString(),
              station.get("stationDistance").toString(),
              station.get("stationDirection").toString(),
              arsSplit(arsId),
              station.get("localStationID").toString(),
              station.get("x").toString(),
              station.get("y").toString()
          );
          busStopRepository.save(busInfo);
        }
      } catch (ParseException e) {
        throw new CustomException(ErrorCode.JSON_PARSE_IMPOSSIBLE);
      }
    }
  }

  @Transactional(readOnly = false)
  public void saveSubway() {
    String fileName = "metroTimeTable.xls";
    Workbook workbook = getSheets(fileName);
    int metroNo = 0;
    for (int idx = 0; idx < 4; idx++) {
      // 파일 내 idx 번째 시트
      Sheet workSheet = workbook.getSheetAt(idx);

      if (idx == 0)
        /* StationInfo 저장 */ {
        for (int r = 5; r < 27; r++) {
          StationInfo stationInfo = StationInfo.createStationInfo(
              workSheet.getRow(r).getCell(1).getStringCellValue()
          );
          stationInfoRepository.save(stationInfo);
        }
      }

      /* MetroInfo 저장 */
      Row metroNoRow = workSheet.getRow(3);
      Row startNameRow = workSheet.getRow(4);
      Row endNameRow = workSheet.getRow(27);
      for (int c = 2; c < metroNoRow.getPhysicalNumberOfCells(); c++) {
        MetroInfo metroInfo = MetroInfo.createMetroInfo(
            String.valueOf((int) metroNoRow.getCell(c).getNumericCellValue()),
            startNameRow.getCell(c).getStringCellValue(),
            endNameRow.getCell(c).getStringCellValue(),
            idx < 2
        );
        metroInfoRepository.save(metroInfo);
        for (int r = 5; r < 27; r++) {
          Object o = checkCell(workSheet.getRow(r).getCell(c));
          if (o == null || o.equals("") || o.equals("false")) {
            continue;
          }
          StationStopInfo stationStopInfo = StationStopInfo.createStationStopInfo(
              r - 4, metroNo + c - 1, o.toString());
          stationStopInfoRepository.save(stationStopInfo);
        }
      }

      metroNo += metroNoRow.getPhysicalNumberOfCells() - 2;
    }
  }

  private Workbook getSheets(String fileName) {
    String fileExtsn = FilenameUtils.getExtension(fileName); // 파일 Original 이름 불러오기 ex) 전문가.xlsx

    Workbook workbook;
    try {
      // 엑셀 97 - 2003 까지는 HSSF(xls),  엑셀 2007 이상은 XSSF(xlsx)
      if (fileExtsn.equals("xls")) {
        workbook = new HSSFWorkbook(getClass().getClassLoader().getResourceAsStream(fileName));
      } else {
        workbook = new XSSFWorkbook(getClass().getClassLoader().getResourceAsStream(fileName));
      }
    } catch (IOException e) {
      throw new CustomException(ErrorCode.ABNORMAL_FILE_READ);
    }
    return workbook;
  }

  public Object checkCell(Cell cell) {
    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
    Object value;
    if (cell == null) {
      return null;
    } else {
      // 타입별로 내용 읽기
      switch (cell.getCellType()) {
        case FORMULA:
          value = cell.getCellFormula();
          break;
        case NUMERIC:
          if (HSSFDateUtil.isCellDateFormatted(cell)) { // 숫자- 날짜 타입이다.
            return LocalTime.parse(formatter.format(cell.getDateCellValue()));
          } else {
            double numericCellValue = cell.getNumericCellValue();
            if (numericCellValue == Math.rint(numericCellValue)) {
              value = String.valueOf((int) numericCellValue);
            } else {
              value = String.valueOf(numericCellValue);
            }
          }
          break;
        case STRING:
          value = cell.getStringCellValue() + "";
          break;
        case BLANK:
          value = cell.getBooleanCellValue() + "";
          break;
        case ERROR:
          value = cell.getErrorCellValue() + "";
          break;
        default:
          value = cell.getStringCellValue();
          break;
      }
    }
    return value;
  }

  private static String arsSplit(String str) {
    StringTokenizer st = new StringTokenizer(str, "-");
    if (st.countTokens() <= 1) {
      return str;
    }
    return st.nextToken() + st.nextToken();
  }
}
