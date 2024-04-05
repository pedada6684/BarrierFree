package com.fullship.hBAF.domain.busInfo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class BusInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Schema(description = "차량 번호판")
  String busRegNo;
  @Schema(description = "저상 유무 (1: 일반, 2: 저상)")
  String busType;
  @Schema(description = "공공 노선 ID 8글자 숫자")
  String routeId;

  public static BusInfo createBusInfo(
      String busRegNo,
      String busType,
      String routeId
  ) {
    BusInfo busInfo = new BusInfo();
    busInfo.busRegNo = busRegNo;
    busInfo.busType = busType;
    busInfo.routeId = routeId;
    return busInfo;
  }
}
