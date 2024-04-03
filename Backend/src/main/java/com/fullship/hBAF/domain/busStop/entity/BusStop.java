package com.fullship.hBAF.domain.busStop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@ToString
public class BusStop {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  String busId;
  String busNo;
  String stopId;
  String stopName;
  String stopDistance;
  String stopDirection;
  String arsId;
  String localStationId;
  String geoLng;
  String geoLat;

  public static BusStop createBusInfo(
      String busId,
      String busNo,
      String stopId,
      String stopName,
      String stopDistance,
      String stopDirection,
      String arsId,
      String localStationId,
      String geoLng,
      String geoLat) {
    BusStop busStopInfo = new BusStop();
    busStopInfo.busId = busId;
    busStopInfo.busNo = busNo;
    busStopInfo.stopId = stopId;
    busStopInfo.stopName = stopName;
    busStopInfo.stopDistance = stopDistance;
    busStopInfo.stopDirection = stopDirection;
    busStopInfo.arsId = arsId;
    busStopInfo.localStationId = localStationId;
    busStopInfo.geoLng = geoLng;
    busStopInfo.geoLat = geoLat;
    return busStopInfo;
  }
}
