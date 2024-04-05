package com.fullship.hBAF.domain.place.service.command;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchPathToTrafficCommand implements Serializable {

  private URI uri;
  private Map<String, Object> requestBody;

}
