package com.fullship.hBAF.global.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeoCode {

  private String latitude;
  private String longitude;
  private String angleSlope;
}
