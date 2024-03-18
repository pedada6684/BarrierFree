package com.fullship.hBAF.domain.member.entity;

import com.fullship.hBAF.domain.bookmarkPlace.entity.BookmarkPlace;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class Member {

  @Id
  @GeneratedValue
  @Column(name = "member_id")
  private Long id;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
  private List<BookmarkPlace> bookmarkPlaces;


}
