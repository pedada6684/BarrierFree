package com.fullship.hBAF.global.api.service;

import com.fullship.hBAF.domain.busInfo.repository.BusInfoRepository;
import com.fullship.hBAF.global.api.response.BusesCurLocation;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Service
@RequiredArgsConstructor
public class TagoApiService {

  private final ApiService<String> apiService;
  private final BusInfoRepository busInfoRepository;

  @Value("${api.data.route.key}")
  private String routeKey;

  @Value("${api.tago.key}")
  private String TagoKey;

  private HttpHeaders setHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  /**
   * 저상 정보 필터링 메서드
   *
   * @param publicBusId: 노선 번호
   * @param direction:   방향(상, 하행)
   * @return BusesCurLocation
   */
  @Transactional(readOnly = true)
  public List<BusesCurLocation> findBusesByPublicId(String publicBusId, String direction) {

    try {
      UriComponents uriComponents = UriComponentsBuilder
          .fromHttpUrl("http://openapitraffic.daejeon.go.kr/api/rest/busposinfo/getBusPosByRtid")
          .queryParam("serviceKey", routeKey)
          .queryParam("busRouteId", publicBusId)
          .build(true);

      ResponseEntity<String> response = apiService.get(uriComponents.toUri(), setHttpHeaders(),
          String.class);

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      Document document = builder.parse(
          new InputSource(new StringReader(response.getBody())));
      document.getDocumentElement().normalize();
      NodeList itemList = document.getElementsByTagName("itemList");
      NodeList busNodeId = document.getElementsByTagName("BUS_NODE_ID");
      NodeList dir = document.getElementsByTagName("DIR");
      NodeList arsId = document.getElementsByTagName("BUS_STOP_ID");
      NodeList license = document.getElementsByTagName("PLATE_NO");
      NodeList gpsLati = document.getElementsByTagName("GPS_LATI");
      NodeList gpsLong = document.getElementsByTagName("GPS_Long");

      List<BusesCurLocation> list = new ArrayList<>();
      for (int i = 0; i < itemList.getLength(); i++) {
        BusesCurLocation command = BusesCurLocation.builder()
            .busNodeId(busNodeId.item(i).getTextContent())
            .dir(dir.item(i).getTextContent())
            .arsId(arsId.item(i).getTextContent())
            .gpsLati(gpsLati.item(i).getTextContent())
            .gpsLong(gpsLong.item(i).getTextContent())
            .license(license.item(i).getTextContent())
            .build();

        if (!direction.equals(command.getDir())) {
          continue;
        }
        if (busInfoRepository.findBusInfoByBusRegNo(license.item(i).getTextContent()) == null) {
          continue;
        }
        list.add(command);
      }

      return list;
    } catch (ParserConfigurationException | IOException | SAXException e) {
      throw new CustomException(ErrorCode.NO_AVAILABLE_API);
    }

  }


}
