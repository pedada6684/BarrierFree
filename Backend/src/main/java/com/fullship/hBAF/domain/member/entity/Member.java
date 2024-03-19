package com.fullship.hBAF.domain.member.entity;

import com.fullship.hBAF.domain.bookmarkPlace.entity.BookmarkPlace;
import com.fullship.hBAF.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
public class Member {

  @Id
  @GeneratedValue
  @Column(name = "member_id")
  private Long id;

  private String email;

  private String name;

  private String nickname;

  private Long role;

  private LocalDateTime regDate;

  private Long status;

  private Long delDate;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
  private List<BookmarkPlace> bookmarkPlaces;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
  private List<Review> reviews;


}
