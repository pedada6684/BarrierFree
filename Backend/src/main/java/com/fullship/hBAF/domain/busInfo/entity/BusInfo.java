package com.fullship.hBAF.domain.busInfo.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class BusInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String license;
    String busType;

    public static BusInfo createBusInfo(
            String license,
            String busType
    ){
        BusInfo busInfo = new BusInfo();
        busInfo.license=license;
        busInfo.busType=busType;
        return busInfo;
    }
}
