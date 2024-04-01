package com.fullship.hBAF.global.api.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusesCurLocation {

  String localStationId;
  String arsId;
  String license;
}
