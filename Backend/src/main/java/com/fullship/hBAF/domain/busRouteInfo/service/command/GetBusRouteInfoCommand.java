package com.fullship.hBAF.domain.busRouteInfo.service.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetBusRouteInfoCommand {

    String routeId;

}
