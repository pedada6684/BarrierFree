package com.fullship.hBAF.global.api.service.command;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OdSayPathCommand {

  private String uri;

  private Map<String, Object> requestBody;

}

