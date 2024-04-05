package com.fullship.hBAF.global.api.service;

import com.fullship.hBAF.domain.busInfo.repository.BusInfoRepository;
import com.fullship.hBAF.domain.busStop.entity.BusStop;
import com.fullship.hBAF.domain.busStop.repository.BusStopRepository;
import com.fullship.hBAF.global.api.response.BusesCurLocation;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Service
@Slf4j
@RequiredArgsConstructor
public class TagoApiService {

  private final ApiService<String> apiService;
  private final BusInfoRepository busInfoRepository;
  private final BusStopRepository busStopRepository;

  @Value("${api.data.route.key}")
  private String routeKey;

  @Value("${api.tago.key}")
  private String TagoKey;

  private HttpHeaders setHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  public List<BusesCurLocation> findBusesByPublicId(String publicBusId, String direction, String busId, Long busStopId){
    try{
      URI uri = UriComponentsBuilder
          .fromHttpUrl("https://apis.data.go.kr/1613000/BusLcInfoInqireService/getRouteAcctoBusLcList")
          .queryParam("serviceKey", TagoKey)
          .queryParam("pageNo", 1)
          .queryParam("numOfRows", 100)
          .queryParam("_type", "xml")
          .queryParam("cityCode", 25)
          .queryParam("routeId", "DJB" + publicBusId)
          .build(true).toUri();

      ResponseEntity<String> response = apiService.get(uri, setHttpHeaders(), String.class);

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      Document document = builder.parse(
          new InputSource(new StringReader(response.getBody())));
      document.getDocumentElement().normalize();
      NodeList item = document.getElementsByTagName("item");
      NodeList busNodeId = document.getElementsByTagName("nodeid");
      NodeList license = document.getElementsByTagName("vehicleno");

      List<BusesCurLocation> list = new ArrayList<>();
      for (int i = 0; i < item.getLength(); i++) {
        BusesCurLocation command = BusesCurLocation.builder()
            .localStationId(busNodeId.item(i).getTextContent())
            .license(license.item(i).getTextContent())
            .build();
        BusStop busStop = busStopRepository.findBusStopByLocalStationIdAndBusIdAndStopDirection(
            command.getLocalStationId(), busId, direction);

        if(busStop == null || !busStop.getStopDirection().equals(direction) || busStopId < busStop.getId())
          continue;

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
