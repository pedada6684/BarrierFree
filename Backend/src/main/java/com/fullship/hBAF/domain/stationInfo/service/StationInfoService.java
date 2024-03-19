package com.fullship.hBAF.domain.stationInfo.service;

import com.fullship.hBAF.domain.stationInfo.repository.StationInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationInfoService {

  private final StationInfoRepository stationInfoRepository;
}
