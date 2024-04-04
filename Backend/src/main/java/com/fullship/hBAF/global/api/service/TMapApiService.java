package com.fullship.hBAF.global.api.service;

import com.fullship.hBAF.global.api.response.TaxiPathForm;
import com.fullship.hBAF.global.api.response.WheelPathForm;
import com.fullship.hBAF.domain.place.service.command.SearchPathToTrafficCommand;
import com.fullship.hBAF.domain.place.service.command.SearchPathToWheelCommand;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
      throw new CustomException(ErrorCode.NO_AVAILABLE_API);
    }
  }


  public TaxiPathForm searchPathToCar(SearchPathToTrafficCommand command){
    ResponseEntity<String> responseEntity =
        apiService.post(command.getUri(), setHttpHeaders(),command.getRequestBody(), String.class);

    TaxiPathForm taxiPathForm = TaxiPathForm.jsonToO(responseEntity);

    if (responseEntity.getStatusCode() == HttpStatus.OK) {
      return taxiPathForm;
    } else {
      throw new CustomException(ErrorCode.NO_AVAILABLE_API);
    }
  }
}

