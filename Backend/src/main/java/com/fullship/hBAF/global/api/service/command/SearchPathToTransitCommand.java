package com.fullship.hBAF.global.api.service.command;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchPathToTransitCommand {

  private String url;
  private Map<String, Object> requestBody;

}
