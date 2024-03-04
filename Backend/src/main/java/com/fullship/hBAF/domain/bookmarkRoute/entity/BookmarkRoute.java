package com.fullship.hBAF.domain.bookmarkRoute.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "bookmarkRoute")
public class BookmarkRoute {

  @Id
  @GeneratedValue
  @Column(name = "bookmark_route_id")
  private Long id;
}
