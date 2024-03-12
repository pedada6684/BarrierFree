package com.fullship.hBAF.global.api.service;

import com.fullship.hBAF.domain.busInfo.entity.BusInfo;
import com.fullship.hBAF.domain.busInfo.repository.BusInfoRepository;
import com.fullship.hBAF.domain.busRouteInfo.entity.BusRouteInfo;
import com.fullship.hBAF.domain.busRouteInfo.repository.BusRouteInfoRepository;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataApiService {

    private final ApiService<String> apiService;
    private final BusInfoRepository busInfoRepository;
    private final BusRouteInfoRepository busRouteInfoRepository;

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

            ResponseEntity<String> response = apiService.get(uriComponents.toUri(), setHttpHeaders(), String.class);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new InputSource(new StringReader(response.getBody().toString())));
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
                    BusInfo busInfoByLicense = busInfoRepository.findBusInfoByLicense(carRegNo.item(j).getTextContent());
                    if(busInfoByLicense != null)
                        continue;
                    BusInfo busInfo = BusInfo.createBusInfo(carRegNo.item(j).getTextContent(), busType.item(j).getTextContent());
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
                    .queryParam("cityCode",25)
                    .build(true);
            ResponseEntity<String> response = apiService.get(uriComponents.toUri(), setHttpHeaders(), String.class);

            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(response.getBody());
            JSONObject res = (JSONObject) object.get("response");
            JSONObject body = (JSONObject) res.get("body");
            JSONObject items = (JSONObject) body.get("items");
            JSONArray arrItems = (JSONArray) items.get("item");

            for(int i = 0; i<arrItems.size(); i++){
                JSONObject item = (JSONObject) arrItems.get(i);
                String startvehicletime;
                if(item.get("startvehicletime") instanceof Long)
                    startvehicletime = String.valueOf(item.get("startvehicletime"));
                else
                    startvehicletime = (String) item.get("startvehicletime");
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


}
