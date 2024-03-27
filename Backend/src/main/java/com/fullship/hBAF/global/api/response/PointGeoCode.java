package com.fullship.hBAF.global.api.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PointGeoCode {
  private String latitude;
  private String longitude;
}
