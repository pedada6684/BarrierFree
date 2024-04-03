package com.fullship.hBAF.global.api.service;

import com.fullship.hBAF.global.api.response.Elevation;
import com.fullship.hBAF.global.api.service.command.ElevationForPathCommand;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleApiService {

  private final ApiService<String> apiService;

  @Value("${api.google.key}")
  private String apiKey;

  private final int maxSample = 512;

  private HttpHeaders setHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  public Elevation elevationForPath(ElevationForPathCommand command) {
    URI uri = UriComponentsBuilder
        .fromHttpUrl("https://maps.googleapis.com/maps/api/elevation/json")
//        .queryParam("locations",
//            URLEncoder.encode(command.getGeoCode(), StandardCharsets.UTF_8))
        .queryParam("path",
            URLEncoder.encode(command.getGeoCode(), StandardCharsets.UTF_8))
        .queryParam("samples", command.getSize() * 1.5 < maxSample ? (int)(command.getSize() * 1.5) : maxSample)
        .queryParam("key", URLEncoder.encode(apiKey, StandardCharsets.UTF_8))
        .build(true).toUri();
    int size = command.getSize() * 1.5 < maxSample ? (int)(command.getSize() * 1.5) : maxSample;
    ResponseEntity<String> response = apiService.get(uri, setHttpHeaders(), String.class);
    Elevation elevation = Elevation.jsonToO(response);

    if (response.getStatusCode() == HttpStatus.OK) {
      return elevation;
    } else {
      throw new CustomException(ErrorCode.NO_AVAILABLE_API);
    }
  }
}
