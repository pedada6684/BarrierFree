package com.fullship.hBAF.domain.busRouteInfo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class BusRouteInfo {

    @Id
    @GeneratedValue
    Long busRouteInfoId;

    String routeId;
    String busId;
    String purpose;
    String startTime;
    String startStop;

    public static BusRouteInfo createBusRouteInfo(
            String routeId,
            String busId,
            String purpose,
            String startTime,
            String startStop
    ){
        BusRouteInfo busRouteInfo = new BusRouteInfo();
        busRouteInfo.routeId=routeId;
        busRouteInfo.busId=busId;
        busRouteInfo.purpose=purpose;
        busRouteInfo.startTime=startTime;
        busRouteInfo.startStop=startStop;

        return busRouteInfo;
    }

}
