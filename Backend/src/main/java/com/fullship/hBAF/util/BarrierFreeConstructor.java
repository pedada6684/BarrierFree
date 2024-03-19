package com.fullship.hBAF.util;

import com.fullship.hBAF.global.api.service.DataApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

  @EventListener(ApplicationReadyEvent.class)
  @Transactional
  @Order(4)
  public void saveSubway(){

    dataApiService.saveSubway();
  }

}