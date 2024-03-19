package com.fullship.hBAF.domain.stationStopInfo.controller;

import com.fullship.hBAF.domain.stationStopInfo.service.StationStopInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StationStopController {
  private final StationStopInfoService stopInfoService;


}
