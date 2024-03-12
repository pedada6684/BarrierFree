package com.fullship.hBAF.global.api.service.command;

import java.net.URI;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchPathToTransitCommand {

  private URI uri;
  private Map<String, Object> requestBody;

}
