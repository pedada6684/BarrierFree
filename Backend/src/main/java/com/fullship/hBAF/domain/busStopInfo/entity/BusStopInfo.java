package com.fullship.hBAF.domain.busStopInfo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class BusStopInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String busStopName;
    String busStopSeq;
    String busStopType;
    String busStopNo;
    String busStopArsNo;
    String routeNo;
    String busNo;
    String busStopLat;
    String busStopLong;

    public static BusStopInfo createBusStopInfo(
            String busStopName,
            String busStopSeq,
            String busStopType,
            String busStopNo,
            String busStopArsNo,
            String routeNo,
            String busNo,
            String busStopLat,
            String busStopLong
    ){
        BusStopInfo busStopInfo = new BusStopInfo();
        busStopInfo.busStopName=busStopName;
        busStopInfo.busStopSeq=busStopSeq;
        busStopInfo.busStopType=busStopType;
        busStopInfo.busStopNo=busStopNo;
        busStopInfo.busStopArsNo=busStopArsNo;
        busStopInfo.routeNo=routeNo;
        busStopInfo.busNo=busNo;
        busStopInfo.busStopLat=busStopLat;
        busStopInfo.busStopLong=busStopLong;

        return busStopInfo;
    }

}
