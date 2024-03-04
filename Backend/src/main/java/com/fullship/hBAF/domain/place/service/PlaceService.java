package com.fullship.hBAF.domain.place.service;

import com.fullship.hBAF.domain.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

  private final PlaceRepository placeRepository;
}
