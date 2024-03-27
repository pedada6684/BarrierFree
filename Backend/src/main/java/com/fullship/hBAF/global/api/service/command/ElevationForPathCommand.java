package com.fullship.hBAF.global.api.service.command;

import com.fullship.hBAF.global.api.response.GeoCode;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElevationForPathCommand {

  private int size;
  private String geoCode;

  public static ElevationForPathCommand createElevateCommand(List<GeoCode> geoCode) {
    StringBuilder sb = new StringBuilder();
    for (GeoCode code : geoCode) {
      sb.append(code.getLatitude()).append(",").append(code.getLongitude()).append("|");
    }

    return ElevationForPathCommand.builder()
        .size(geoCode.size())
        .geoCode(sb.substring(0, sb.length() - 1))
        .build();
  }
}
