package com.fullship.hBAF.global.api.service.command;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElevationForPathCommand {

  private String geoCode;

  public static ElevationForPathCommand createElevateCommand(List<String[]> geoCode) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < geoCode.size(); i++) {
      sb.append(geoCode.get(i)[1]).append(",").append(geoCode.get(i)[0]).append("|");
    }

    return ElevationForPathCommand.builder()
        .geoCode(sb.toString().substring(0, sb.length() - 1))
        .build();
  }
}
