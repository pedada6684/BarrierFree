package com.fullship.hBAF.domain.busRouteInfo.service.command;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class GetBusRouteInfoCommand {

    String routeName;

}
