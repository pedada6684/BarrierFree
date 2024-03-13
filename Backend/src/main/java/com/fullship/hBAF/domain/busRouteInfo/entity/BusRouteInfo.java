package com.fullship.hBAF.domain.busRouteInfo.entity;

import com.fullship.hBAF.domain.busStopInfo.entity.BusStopInfo;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class BusRouteInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String routeNo;
    String busNo;
    String purpose;
    String startTime;
    String startStop;

    @OneToMany
    List<BusStopInfo> busStopInfo = new ArrayList<>();

    public static BusRouteInfo createBusRouteInfo(
            String routeNo,
            String busNo,
            String purpose,
            String startTime,
            String startStop
    ){
        BusRouteInfo busRouteInfo = new BusRouteInfo();
        busRouteInfo.routeNo=routeNo;
        busRouteInfo.busNo=busNo;
        busRouteInfo.purpose=purpose;
        busRouteInfo.startTime=startTime;
        busRouteInfo.startStop=startStop;

        return busRouteInfo;
    }

}
