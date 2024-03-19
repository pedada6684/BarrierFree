package com.fullship.hBAF.global.api.service;

import com.fullship.hBAF.global.api.response.OdSayPath;
import com.fullship.hBAF.global.api.response.PathGeoCode;
import com.fullship.hBAF.global.api.service.command.OdSayGeoCommand;
import com.fullship.hBAF.global.api.service.command.OdSayPathCommand;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
public class OdSayApiService {

  private final ApiService<String> apiService;

  @Value("${api.odSay.key}")
  private String apiKey;

  private HttpHeaders setHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  public List<OdSayPath> searchPathToTransit(OdSayPathCommand command) {
    URI uri;
    try {
      uri = new URI(command.getUri() + "&apiKey=" + URLEncoder.encode(apiKey,
          StandardCharsets.UTF_8));
    } catch (Exception e) {
      throw new CustomException(ErrorCode.URI_SYNTAX_ERROR);
    }

    ResponseEntity<String> responseEntity =
        apiService.get(uri, setHttpHeaders(), String.class);

    List<OdSayPath> odSayPaths = OdSayPath.jsonToO(responseEntity);

    if (responseEntity.getStatusCode() == HttpStatus.OK) {
      return odSayPaths;
    } else {
      throw new CustomException(ErrorCode.NO_AVAILABLE_API);
    }
  }

  public List<PathGeoCode> getOdSayGeoCode(OdSayGeoCommand command) {
    URI uri;
    try {
      uri = new URI(command.getUri() + "&apiKey=" + URLEncoder.encode(apiKey,
          StandardCharsets.UTF_8) + command.getMapObj());
    } catch (Exception e) {
      throw new CustomException(ErrorCode.URI_SYNTAX_ERROR);
    }

    ResponseEntity<String> responseEntity =
        apiService.get(uri, setHttpHeaders(), String.class);

    List<PathGeoCode> geoCode = PathGeoCode.jsonToO(responseEntity);
    if (responseEntity.getStatusCode() == HttpStatus.OK) {
      return geoCode;
    } else {
      throw new CustomException(ErrorCode.NO_AVAILABLE_API);
    }
  }

}
