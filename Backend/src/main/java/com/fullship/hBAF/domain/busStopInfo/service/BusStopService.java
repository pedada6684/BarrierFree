package com.fullship.hBAF.domain.busStopInfo.service;

import com.fullship.hBAF.domain.busStopInfo.entity.BusStopInfo;
import com.fullship.hBAF.domain.busStopInfo.repository.BusStopRepository;
import com.fullship.hBAF.domain.busStopInfo.service.command.request.GetDirectionRequestCommand;
import com.fullship.hBAF.domain.busStopInfo.service.command.request.GetNearBusStopRequestCommand;
import com.fullship.hBAF.domain.busStopInfo.service.command.response.GetNearBusStopResponseCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusStopService {

    private final BusStopRepository busStopRepository;

    public List<GetNearBusStopResponseCommand> getNearBusStops(GetNearBusStopRequestCommand command){
        double lng = Double.parseDouble(command.getLng());
        double lat = Double.parseDouble(command.getLat());

        List<BusStopInfo> list = new ArrayList<>();
        list = busStopRepository.findAllByRouteNo(command.getRouteNo());

        List<GetNearBusStopResponseCommand> commandList = new ArrayList<>();
        for(BusStopInfo busStop : list){
            double diff = Math.abs(lng-Double.parseDouble(busStop.getBusStopLong()))-Math.abs(lat-Double.parseDouble(busStop.getBusStopLat()));
            GetNearBusStopResponseCommand busCommand = GetNearBusStopResponseCommand.builder()
                    .busStopNo(busStop.getBusStopNo())
                    .busStopName(busStop.getBusStopName())
                    .lng(busStop.getBusStopLong())
                    .lat(busStop.getBusStopLat())
                    .seq(busStop.getBusStopSeq())
                    .dir(busStop.getBusStopType())
                    .diff(diff).build();
            commandList.add(busCommand);
        }

        Collections.sort(commandList, (a, b) -> Double.compare(a.getDiff(), b.getDiff()));

        return commandList;
    }

    @Transactional(readOnly = true)
    public String getDirection(GetDirectionRequestCommand command){
        GetNearBusStopRequestCommand startCommand = GetNearBusStopRequestCommand.builder()
                .routeNo(command.getRouteNo())
                .lat(command.getSLat())
                .lng(command.getSLng())
                .build();
        GetNearBusStopRequestCommand endCommand = GetNearBusStopRequestCommand.builder()
                .routeNo(command.getRouteNo())
                .lat(command.getELat())
                .lng(command.getELng())
                .build();

        List<GetNearBusStopResponseCommand> startList = getNearBusStops(startCommand);
        List<GetNearBusStopResponseCommand> endList = getNearBusStops(endCommand);

        int sSeq = 0;
        int eSeq = 0;
        int sDir = 0;
        int eDir = 0;
        for(GetNearBusStopResponseCommand startBusStop : startList){
            sSeq = Integer.parseInt(startBusStop.getSeq());
            sDir = Integer.parseInt(startBusStop.getDir());
            for(GetNearBusStopResponseCommand endBusStop : endList){
                eSeq = Integer.parseInt(endBusStop.getSeq());
                if(sSeq>=eSeq)
                    continue;

                eDir = Integer.parseInt(endBusStop.getDir());
                if(sDir == eDir || (sDir==2 && sDir<eDir) || (eDir==2 && sDir==1)){
                    if(sDir==1)
                        return "0";
                    else
                        return "1";
                }
            }
        }
        return "-1";
    }


}