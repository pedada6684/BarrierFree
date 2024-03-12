package com.fullship.hBAF.domain.place.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "image")
public class Image {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "image_id")
  private Long id;
  private String imageUrl;
  private Integer imageType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "place_id")
  private Place place;

  public static Image createNewImage(
    Place place,
    String imageUrl,
    int imageType
  ){
    Image image = new Image();
    image.place = place;
    image.imageUrl = imageUrl;
    image.imageType = imageType;
    return image;
  };
  public void updateImageUrl(String url) {
    imageUrl = url;
  }
}
