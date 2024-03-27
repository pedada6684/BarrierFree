package com.fullship.hBAF.global.api.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeoCode {

  private String latitude;
  private String longitude;
  private String angleSlope;
}
