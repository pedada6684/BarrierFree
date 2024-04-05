package com.fullship.hBAF.global.api.service;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ApiService<T> {

  private final RestTemplate restTemplate;

  public ResponseEntity<T> get(URI uri, HttpHeaders headers) {
    return callApiEndpoint(uri, HttpMethod.GET, headers, null, (Class<T>) Object.class);
  }

  public ResponseEntity<T> get(URI uri, HttpHeaders headers, Class<T> clazz) {
    return callApiEndpoint(uri, HttpMethod.GET, headers, null, clazz);
  }

  public ResponseEntity<T> post(URI uri, HttpHeaders headers, Object body) {
    return callApiEndpoint(uri, HttpMethod.POST, headers, body, (Class<T>) Object.class);
  }

  public ResponseEntity<T> post(URI uri, HttpHeaders headers, Object body, Class<T> clazz) {
    return callApiEndpoint(uri, HttpMethod.POST, headers, body, clazz);
  }

  private ResponseEntity<T> callApiEndpoint(URI uri, HttpMethod method, HttpHeaders headers,
      Object body, Class<T> tClass) {
    return restTemplate.exchange(uri, method, new HttpEntity<>(body, headers), tClass);
  }
}
