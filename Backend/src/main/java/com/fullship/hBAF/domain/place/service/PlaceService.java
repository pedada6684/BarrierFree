package com.fullship.hBAF.domain.place.service;

import com.fullship.hBAF.domain.place.controller.response.PlaceListResonse;
import com.fullship.hBAF.domain.place.entity.Image;
import com.fullship.hBAF.domain.place.entity.Place;
import com.fullship.hBAF.domain.place.repository.ImageRepository;
import com.fullship.hBAF.domain.place.repository.PlaceRepository;
import com.fullship.hBAF.domain.place.service.command.CreatePlaceCommand;
import com.fullship.hBAF.domain.place.service.command.UpdatePlaceImageCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

  private final PlaceRepository placeRepository;
  private final ImageRepository imageRepository;

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
            command.getBarrierFree(),
            command.getType()
    );

    if (command.getType()){
      newPlace.insertWtcltId(command.getWtcltId());
    }
    //검색결과를 기반으로 save함
    return placeRepository.save(newPlace).getId();
  }

  @Transactional(readOnly = false)
  public Long updatePlaceImageUrl(UpdatePlaceImageCommand command){
    Place place = placeRepository.findById(command.getPlaceId()).orElseThrow(() -> new IllegalStateException());
    Optional<Image> imageOptional = imageRepository.findByPlaceAndImageType(place, 0);
    if (imageOptional.isPresent()){
      Image image = imageOptional.get();
      image.updateImageUrl(command.getImageUrl());
    }else { // 썸네일 이미지 없는 생성
      Image newImage = Image.createNewImage(place, command.getImageUrl(), 0);
      imageRepository.save(newImage);
    }
    return place.getId();
  }

  /**
   * 장애인 시설 카테고리별 불러오기
   * @param category
   * @return
   */
  public List<PlaceListResonse> getPlaceByCategory(String category) {
    List<Place> placeEntityList = placeRepository.findByCategory(category);

    List<PlaceListResonse> placeList = new ArrayList<>();
    for (Place place : placeEntityList) {
      placeList.add(PlaceListResonse.from(place));
    }

    return placeList;
  }
}