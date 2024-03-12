package com.fullship.hBAF.domain.busRouteInfo.service;

import com.fullship.hBAF.domain.busRouteInfo.entity.BusRouteInfo;
import com.fullship.hBAF.domain.busRouteInfo.repository.BusRouteInfoRepository;
import com.fullship.hBAF.domain.busRouteInfo.service.command.GetBusRouteInfoCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusRouteService {

    private final BusRouteInfoRepository busRouteInfoRepository;

    public GetBusRouteInfoCommand getBusRouteInfo(String busId){

        BusRouteInfo busRouteInfo = busRouteInfoRepository.findBusRouteInfoByBusId(busId);
        GetBusRouteInfoCommand command = GetBusRouteInfoCommand.builder()
                .routeId(busRouteInfo.getRouteId().substring(3)).build();
        return command;

    }

}
