package com.fullship.hBAF.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "member")
public class Member {

  @Id
  @GeneratedValue
  @Column(name = "member_id")
  private Long id;
}
