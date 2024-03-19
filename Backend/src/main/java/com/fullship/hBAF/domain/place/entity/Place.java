package com.fullship.hBAF.domain.place.entity;

import com.fullship.hBAF.domain.place.service.PlaceService;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "place")
public class Place {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "place_id")
  private Long id;
  private String placeName;
  private String address;
  private String latitude;
  private String longitude;
  private String poiId;
  private String category;
  private String barrierFree;
  private boolean type; // 배리어프리 장소인지 여부
  private String wtcltId;

  @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Image> images;

  public static Place createNewPlace(
          String placeName,
          String address,
          String latitude,
          String longitude,
          String poiId,
          String category,
          String barrierFree,
          String wtcltId,
          boolean type
  ){
    Place place = new Place();
    place.placeName = placeName;
    place.address = address;
    place.latitude = latitude;
    place.longitude = longitude;
    place.poiId = poiId;
    place.category = category;
    place.barrierFree = barrierFree;
    place.images = new ArrayList<>();
    place.wtcltId = wtcltId;
    place.type = type;
    return place;
  };

  public void insertWtcltId(String wtcltId) {
    this.wtcltId = wtcltId;
  }
  public void updateBarrierFree(String value) {this.barrierFree = value;}
  public void addImage(Image image){
    this.images.add(image);
  }
}
