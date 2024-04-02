package com.fullship.hBAF.global.api.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointGeoCode {
  private String latitude;
  private String longitude;
}
