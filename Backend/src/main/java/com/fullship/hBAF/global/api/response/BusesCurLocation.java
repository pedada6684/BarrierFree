package com.fullship.hBAF.global.api.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusesCurLocation {

  String busNodeId;
  String dir;
  String arsId;
  String gpsLati;
  String gpsLong;
  String license;
}
