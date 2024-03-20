package com.fullship.hBAF.domain.member.entity;

import com.fullship.hBAF.domain.bookmarkPlace.entity.BookmarkPlace;
import com.fullship.hBAF.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

  @Id
  @GeneratedValue
  @Column(name = "member_id")
  private Long id;
  @Column(unique = true)
  private String email;
  private String password;
  private String nickname;
  private String username;
  private Long role;
  private LocalDateTime regDate;
  private Long status;
  private Long delDate;
  private OAuthProvider provider;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
  private List<BookmarkPlace> bookmarkPlaces;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
  private List<Review> reviews;

  public static Member createNewMember(
          String email,
          String password,
          String nickname,
          String username
  ){
    Member member = new Member();
    member.email = email;
    member.password = password;
    member.nickname = nickname;
    member.username = username;
    return member;
  }
}
