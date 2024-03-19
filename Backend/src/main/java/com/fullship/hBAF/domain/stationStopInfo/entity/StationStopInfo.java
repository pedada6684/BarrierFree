package com.fullship.hBAF.domain.stationStopInfo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalTime;
import lombok.Getter;

@Entity
@Getter
public class StationStopInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private int subwayInfoId;

  private int metroInfoId;

  private LocalTime arrTime;

  public static StationStopInfo createStationStopInfo(int subwayInfoId, int metroInfoId,
      LocalTime arrTime) {
    StationStopInfo stationStopInfo = new StationStopInfo();
    stationStopInfo.subwayInfoId = subwayInfoId;
    stationStopInfo.metroInfoId = metroInfoId;
    stationStopInfo.arrTime = arrTime;
    return stationStopInfo;
  }
}
