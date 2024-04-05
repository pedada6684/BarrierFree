package com.fullship.hBAF.global.api.service.command;

import com.fullship.hBAF.global.api.response.GeoCode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElevationForPathCommand {

  private int size;
  private String geoCode;

  public static ElevationForPathCommand createElevateCommand(List<GeoCode> geoCode) {
    StringBuilder sb = new StringBuilder();
    Set<GeoCode> set = new HashSet<>();

    for (GeoCode code : geoCode) {
      if(set.contains(code))
        continue;
      set.add(code);
      sb.append(code.getLatitude()).append(",").append(code.getLongitude()).append("|");
    }

    return ElevationForPathCommand.builder()
        .size(geoCode.size())
        .geoCode(sb.substring(0, sb.length() - 1))
        .build();
  }
}
