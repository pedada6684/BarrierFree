package com.fullship.hBAF.domain.busStopInfo.service.command.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetNearBusStopResponseCommand {

    String busStopNo;
    String busStopName;
    String lat;
    String lng;

}
