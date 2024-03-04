package com.fullship.hBAF.domain.bookmarkPlace.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "bookmarkPlace")
public class BookmarkPlace {

  @Id
  @GeneratedValue
  @Column(name = "bookmark_place_id")
  private Long id;
}
