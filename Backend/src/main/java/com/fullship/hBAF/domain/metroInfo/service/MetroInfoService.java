package com.fullship.hBAF.domain.metroInfo.service;

import com.fullship.hBAF.domain.metroInfo.repository.MetroInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MetroInfoService {

  private final MetroInfoRepository metroInfoRepository;
}
