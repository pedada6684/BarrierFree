package com.fullship.hBAF.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fullship.hBAF.domain.place.service.PlaceService;
import com.fullship.hBAF.global.api.service.DataApiService;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class BarrierFreeConstructor {

    private final DataApiService dataApiService;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    @Order(1)
    public void saveBusInfo() {
        dataApiService.saveBusInfo();
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    @Order(2)
    public void saveBusRouteInfo() {
        dataApiService.saveRoute();
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    @Order(3)
    public void saveBusStop(){

        dataApiService.saveBusStop();
    }

}