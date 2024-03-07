package com.fullship.hBAF.domain.place.service;

import com.fullship.hBAF.domain.place.entity.Place;
import com.fullship.hBAF.domain.place.repository.PlaceRepository;
import com.fullship.hBAF.domain.place.service.command.CreatePlaceCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

  private final PlaceRepository placeRepository;

  @Transactional(readOnly = false)
  public Long createPlace(CreatePlaceCommand command){
    //poiId를 통해 존재확인
    if(placeRepository.existsByPoiId(command.getPoiId())){
        return null;
//      throw new IllegalStateException("이미 존재하는 장소 :" + command.getPoiId()+" "+command.getPlaceName());
    }
    Place newPlace = Place.createNewPlace(
            command.getPlaceName(),
            command.getAddress(),
            command.getLatitude(),
            command.getLongitude(),
            command.getPoiId(),
            command.getCategory(),
            command.getBarrierFree()
    );
    if (command.getBarrierFree()){
      newPlace.insertWtcltId(command.getWtcltId());
    }
    //검색결과를 기반으로 entity를 만들고 save함
    return placeRepository.save(newPlace).getId();
  }
}
