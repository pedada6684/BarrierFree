package com.fullship.hBAF.domain.busStopInfo.service.command.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetDirectionRequestCommand {

    String routeNo;
    String sLat;
    String sLng;
    String eLat;
    String eLng;

}
