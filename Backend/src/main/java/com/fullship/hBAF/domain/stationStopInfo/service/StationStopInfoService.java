package com.fullship.hBAF.domain.stationStopInfo.service;

import com.fullship.hBAF.domain.stationStopInfo.repository.StationStopInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationStopInfoService {

  private final StationStopInfoRepository stationStopInfoRepository;
}
