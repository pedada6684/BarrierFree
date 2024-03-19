package com.fullship.hBAF.domain.busRouteInfo.entity;

import com.fullship.hBAF.domain.busStop.entity.BusStop;
import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;

import java.util.ArrayList;

@Entity
@Getter
public class BusRouteInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  /* 간단 정보 */
  String busNo;
  String publicBusId;
  String purpose;
  /* 세부 정보 */
  String busId;
  String startPoint;
  String endPoint;
  String firstTime;
  String lastTime;
  String busInterval;

  @OneToMany
  List<BusStop> busStopInfo = new ArrayList<>();

  public static BusRouteInfo createBusRouteInfo(
      String busNo,
      String publicBusId,
      String purpose
  ) {
    BusRouteInfo busRouteInfo = new BusRouteInfo();
    busRouteInfo.busNo = busNo;
    busRouteInfo.publicBusId = publicBusId;
    busRouteInfo.purpose = purpose;

    return busRouteInfo;
  }

  public void updateBusRouteInfo(String busId, String startPoint, String endPoint, String firstTime,
      String lastTime, String busInterval) {
    this.busId = busId;
    this.startPoint = startPoint;
    this.endPoint = endPoint;
    this.firstTime = firstTime;
    this.lastTime = lastTime;
    this.busInterval = busInterval;
  }
}
