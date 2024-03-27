package com.fullship.hBAF.domain.place.controller.request;

import com.fullship.hBAF.domain.place.service.command.Request.AngleSlopeCommand;
import com.fullship.hBAF.global.api.response.PathGeoCode;
import java.util.List;
import lombok.Data;

@Data
public class CalculateAngleRequest {

  private List<PathGeoCode> pathGeoCodes;

  public AngleSlopeCommand createForCalculate(){
    return AngleSlopeCommand.builder().geoCodes(pathGeoCodes).build();
  }
}
