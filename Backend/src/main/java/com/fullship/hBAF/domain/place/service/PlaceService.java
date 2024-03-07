package com.fullship.hBAF.domain.place.service;

import com.fullship.hBAF.domain.place.entity.Place;
import com.fullship.hBAF.domain.place.repository.PlaceRepository;
import com.fullship.hBAF.global.api.response.TransitPathForm;
import com.fullship.hBAF.global.api.service.TMapApiService;
import com.fullship.hBAF.global.api.service.command.SearchPathToTransitCommand;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.fullship.hBAF.domain.place.service.command.CreatePlaceCommand;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
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
  private final TMapApiService tMapApiService;

  public String useTransitPath(SearchPathToTransitCommand command) throws ParseException {
    List<TransitPathForm> list = tMapApiService.searchPathToTransit(command);

    List<TransitPathForm> transferList = new ArrayList<>(list);
    Collections.sort(transferList,
        (o1, o2) -> (int) (o1.getTransferCount() - o2.getTransferCount()));

    int idx = 0;
    long min = transferList.get(0).getTransferCount();
    for (int i = 0; i < transferList.size(); i++, idx++) {
      if(transferList.get(i).getTransferCount() > min + 1)
        break;
    }

    for(int i = idx; i < transferList.size(); i++){
      transferList.remove(idx);
    }



    return null;
  }

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
