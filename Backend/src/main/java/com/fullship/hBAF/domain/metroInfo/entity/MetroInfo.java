package com.fullship.hBAF.domain.metroInfo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class MetroInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String metroNo;
  private String startStationName;
  private String endStationName;
  private boolean isWeekday;

  public static MetroInfo createMetroInfo(String metroNo, String startStationName,
      String endStationName,
      boolean isWeekday) {
    MetroInfo metroInfo = new MetroInfo();
    metroInfo.metroNo = metroNo;
    metroInfo.startStationName = startStationName;
    metroInfo.endStationName = endStationName;
    metroInfo.isWeekday = isWeekday;
    return metroInfo;
  }
}
