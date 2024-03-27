package com.fullship.hBAF.domain.place.service.command.Request;

import com.fullship.hBAF.global.api.response.PathGeoCode;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AngleSlopeCommand {
  public List<PathGeoCode> geoCodes;
}
