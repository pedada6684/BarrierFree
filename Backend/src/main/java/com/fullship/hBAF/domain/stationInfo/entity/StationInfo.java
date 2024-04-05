package com.fullship.hBAF.domain.stationInfo.entity;

import com.fullship.hBAF.domain.stationStopInfo.entity.StationStopInfo;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Getter;

@Entity
@Getter
public class StationInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String subwayName;

  @OneToMany
  List<StationStopInfo> stationStopInfoList;

  public static StationInfo createStationInfo(
      String subwayName){
    StationInfo stationInfo = new StationInfo();
    stationInfo.subwayName=subwayName;
    return stationInfo;
  }
}
