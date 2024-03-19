package com.fullship.hBAF.global.api.service;

import com.fullship.hBAF.domain.busInfo.entity.BusInfo;
import com.fullship.hBAF.domain.busInfo.repository.BusInfoRepository;
import com.fullship.hBAF.domain.busRouteInfo.entity.BusRouteInfo;
import com.fullship.hBAF.domain.busRouteInfo.repository.BusRouteInfoRepository;
import com.fullship.hBAF.domain.busStopInfo.entity.BusStopInfo;
import com.fullship.hBAF.domain.busStopInfo.repository.BusStopRepository;
import com.fullship.hBAF.domain.metroInfo.entity.MetroInfo;
import com.fullship.hBAF.domain.metroInfo.repository.MetroInfoRepository;
import com.fullship.hBAF.domain.stationInfo.entity.StationInfo;
import com.fullship.hBAF.domain.stationInfo.repository.StationInfoRepository;
import com.fullship.hBAF.domain.stationStopInfo.entity.StationStopInfo;
import com.fullship.hBAF.domain.stationStopInfo.repository.StationStopInfoRepository;
import com.fullship.hBAF.global.api.response.BusesCurLocation;
import com.fullship.hBAF.global.api.service.command.BusesCurLocationCommand;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.multipart.MultipartFile;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
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

  private HttpHeaders setHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  @Transactional(readOnly = false)
  public void saveBusInfo() {

    try {
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
          new InputSource(new StringReader(response.getBody().toString())));
      document.getDocumentElement().normalize();
      NodeList itemPageCnt = document.getElementsByTagName("itemPageCnt");

      for (int i = 1; i <= Integer.parseInt(itemPageCnt.item(0).getTextContent()); i++) {
        uriComponents = UriComponentsBuilder
            .fromHttpUrl("http://openapitraffic.daejeon.go.kr/api/rest/busreginfo/getBusRegInfoAll")
            .queryParam("serviceKey", dataLicenseKey)
            .queryParam("reqPage", i)
            .build(true);
        response = apiService.get(uriComponents.toUri(), setHttpHeaders(), String.class);

        document = builder.parse(new InputSource(new StringReader(response.getBody().toString())));
        document.getDocumentElement().normalize();
        NodeList busType = document.getElementsByTagName("BUS_TYPE");
        NodeList carRegNo = document.getElementsByTagName("CAR_REG_NO");
        for (int j = 0; j < busType.getLength(); j++) {
          BusInfo busInfoByLicense = busInfoRepository.findBusInfoByLicense(
              carRegNo.item(j).getTextContent());
          if (busInfoByLicense != null) {
            continue;
          }
          BusInfo busInfo = BusInfo.createBusInfo(carRegNo.item(j).getTextContent(),
              busType.item(j).getTextContent());
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

      for (int i = 0; i < arrItems.size(); i++) {
        JSONObject item = (JSONObject) arrItems.get(i);
        String startvehicletime;
        if (item.get("startvehicletime") instanceof Long) {
          startvehicletime = String.valueOf(item.get("startvehicletime"));
        } else {
          startvehicletime = (String) item.get("startvehicletime");
        }
        BusRouteInfo busRouteInfo = BusRouteInfo.createBusRouteInfo(
            (String) item.get("routeid"),
            String.valueOf(item.get("routeno")),
            (String) item.get("routetp"),
            startvehicletime,
            (String) item.get("startnodenm")
        );

        busRouteInfoRepository.save(busRouteInfo);

      }

    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

  }

  @Transactional(readOnly = false)
  public void saveBusStop() {
    List<BusRouteInfo> busRouteInfoList = busRouteInfoRepository.findAll();
    System.out.println(busRouteInfoList.size());
    try {

      for (BusRouteInfo i : busRouteInfoList) {
        UriComponents uriComponents = UriComponentsBuilder
            .fromHttpUrl(
                "http://openapitraffic.daejeon.go.kr/api/rest/busRouteInfo/getStaionByRoute")
            .queryParam("serviceKey", routeKey)
            .queryParam("busRouteId", i.getRouteNo().substring(3))
            .build(true);
        System.out.println(uriComponents.toUri());
        ResponseEntity<String> response = apiService.get(uriComponents.toUri(), setHttpHeaders(),
            String.class);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(
            new InputSource(new StringReader(response.getBody().toString())));
        document.getDocumentElement().normalize();
        NodeList itemList = document.getElementsByTagName("itemList");
        NodeList busStopName = document.getElementsByTagName("BUSSTOP_NM");
        NodeList busStopSeq = document.getElementsByTagName("BUSSTOP_SEQ");
        NodeList busStopType = document.getElementsByTagName("BUSSTOP_TP");
        NodeList busStopNo = document.getElementsByTagName("BUS_NODE_ID");
        NodeList busStopArsNo = document.getElementsByTagName("BUS_STOP_ID");
        NodeList busStopLat = document.getElementsByTagName("GPS_LATI");
        NodeList busStopLong = document.getElementsByTagName("GPS_LONG");

        int max = 0;
        for (int j = 0; j < itemList.getLength(); j++) {
          String str;
          int num = 0;
          if (!Objects.equals(str = busStopType.item(j).getTextContent(), " ")) {
            num = Integer.parseInt(str);
          }
          max = Math.max(max, num);
          BusStopInfo busStopInfo = BusStopInfo.createBusStopInfo(
              busStopName.item(j).getTextContent(),
              busStopSeq.item(j).getTextContent(),
              String.valueOf(max),
              busStopNo.item(j).getTextContent(),
              busStopArsNo.item(j).getTextContent(),
              i.getRouteNo().substring(3),
              i.getBusNo(),
              busStopLat.item(j).getTextContent(),
              busStopLong.item(j).getTextContent());
          busStopRepository.save(busStopInfo);
          if (max == 2) {
            max++;
          }
          busRouteInfoRepository.getReferenceById(i.getId()).getBusStopInfo().add(busStopInfo);
        }
        break;
      }

    } catch (ParserConfigurationException | IOException | SAXException e) {
      throw new RuntimeException(e);
    }

  }

  @Transactional(readOnly = true)
  public BusesCurLocation findBusesOnRouteName(String routeName, String direction) {

    try {

      UriComponents uriComponents = UriComponentsBuilder
          .fromHttpUrl("http://openapitraffic.daejeon.go.kr/api/rest/busposinfo/getBusPosByRtid")
          .queryParam("serviceKey", routeKey)
          .queryParam("busRouteId", routeName)
          .build(true);

      ResponseEntity<String> response = apiService.get(uriComponents.toUri(), setHttpHeaders(),
          String.class);

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      Document document = builder.parse(
          new InputSource(new StringReader(response.getBody().toString())));
      document.getDocumentElement().normalize();
      NodeList itemList = document.getElementsByTagName("itemList");
      NodeList busNodeId = document.getElementsByTagName("BUS_NODE_ID");
      NodeList dir = document.getElementsByTagName("DIR");
      NodeList plateNo = document.getElementsByTagName("PLATE_NO");

      List<BusesCurLocationCommand> list = new ArrayList<>();
      for (int i = 0; i < itemList.getLength(); i++) {
        BusesCurLocationCommand command = BusesCurLocationCommand.builder()
            .busNodeId(busNodeId.item(i).getTextContent())
            .dir(dir.item(i).getTextContent())
            .license(plateNo.item(i).getTextContent())
            .build();

        if (!direction.equals(command.getDir())) {
          continue;
        }
        if (busInfoRepository.findBusInfoByLicense(command.getLicense()) == null) {
          continue;
        }
        list.add(command);
      }

      BusesCurLocation busesCurLocation = BusesCurLocation.builder()
          .list(list)
          .build();

      return busesCurLocation;
    } catch (ParserConfigurationException | IOException | SAXException e) {
      throw new CustomException(ErrorCode.NO_AVAILABLE_API);
    }

  }

  @Transactional(readOnly = false)
  public void saveSubway() {
    Workbook workbook = getSheets();
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
            idx < 2 ? true : false
        );
        metroInfoRepository.save(metroInfo);

        for (int r = 5; r < 27; r++) {
          String arrTime = (String) checkCell(workSheet.getRow(r).getCell(c));
          if (arrTime == null) {
            continue;
          }
          StationStopInfo stationStopInfo = StationStopInfo.createStationStopInfo(
              r - 4, metroNo + c - 1, arrTime);
          stationStopInfoRepository.save(stationStopInfo);
        }
      }

      metroNo += metroNoRow.getPhysicalNumberOfCells() - 2;
    }
  }

  private Workbook getSheets() {
    String filePath = "C:/Users/Jaesin/Downloads/metroTimeTable.xls";

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

  public Object checkCell(Cell cell) {
    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
    String value = "";
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
            value = formatter.format(cell.getDateCellValue());
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

}
