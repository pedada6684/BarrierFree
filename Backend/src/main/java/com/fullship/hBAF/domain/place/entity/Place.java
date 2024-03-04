package com.fullship.hBAF.domain.place.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "place")
public class Place {

  @Id
  @GeneratedValue
  @Column(name = "place_id")
  private Long id;
}
