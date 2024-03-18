package com.fullship.hBAF.domain.bookmarkPlace.entity;

import com.fullship.hBAF.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class BookmarkPlace {

  @Id
  @GeneratedValue
  @Column(name = "bookmark_place_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  private String poiId;

  public static BookmarkPlace createTobookmarkPlace(
          Member member,
          String poiId
  ){
    BookmarkPlace bookmarkPlace = new BookmarkPlace();
    bookmarkPlace.member=member;
    bookmarkPlace.poiId=poiId;

    return bookmarkPlace;
  }
}
