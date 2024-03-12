package com.fullship.hBAF.global.api.service;

import com.fullship.hBAF.global.api.response.TransitPathForm;
import com.fullship.hBAF.global.api.response.WheelPathForm;
import com.fullship.hBAF.global.api.service.command.SearchPathToTransitCommand;
import com.fullship.hBAF.global.api.service.command.SearchPathToWheelCommand;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.net.URI;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
public class TMapApiService {

  @Value("${api.tmap.key}")
  private String TMapKey;

  private final ApiService<String> apiService;

  private HttpHeaders setHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("appKey", TMapKey);
    return headers;
  }

  public WheelPathForm searchPathToWheel(SearchPathToWheelCommand command) {
    ResponseEntity<String> responseEntity =
        apiService.post(command.getUri(), setHttpHeaders(), command.getRequestBody(), String.class);

    WheelPathForm wheelPathForm = WheelPathForm.jsonToO(responseEntity);

    if (responseEntity.getStatusCode() == HttpStatus.OK) {
      return wheelPathForm;
    } else {
//      System.out.println("API 호출 실패: " + responseEntity.getStatusCode());
      throw new CustomException(ErrorCode.NO_AVAILABLE_API);
    }
  }

  public List<TransitPathForm> searchPathToTransit(SearchPathToTransitCommand command) {
    ResponseEntity<String> responseEntity =
        apiService.post(command.getUri(), setHttpHeaders(), command.getRequestBody(), String.class);

    List<TransitPathForm> transitPathList = TransitPathForm.jsonToO(responseEntity);

    if (responseEntity.getStatusCode() == HttpStatus.OK) {
      return transitPathList;
    } else {
//      System.out.println("API 호출 실패: " + responseEntity.getStatusCode());
      throw new CustomException(ErrorCode.NO_AVAILABLE_API);
    }
  }
}

