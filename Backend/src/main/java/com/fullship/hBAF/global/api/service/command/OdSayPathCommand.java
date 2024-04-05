package com.fullship.hBAF.global.api.service.command;

import java.io.Serializable;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OdSayPathCommand implements Serializable {

  private String type;
  private String uri;
  private Map<String, Object> requestBody;

}

