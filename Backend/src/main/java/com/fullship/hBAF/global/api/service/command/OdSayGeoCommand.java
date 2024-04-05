package com.fullship.hBAF.global.api.service.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OdSayGeoCommand {
  private String uri;

  private String mapObj;
}
