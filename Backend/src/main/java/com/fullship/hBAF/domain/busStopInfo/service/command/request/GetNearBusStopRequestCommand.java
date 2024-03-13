package com.fullship.hBAF.domain.busStopInfo.service.command.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetNearBusStopRequestCommand {

    String routeNo;
    String lat;
    String lng;

}
