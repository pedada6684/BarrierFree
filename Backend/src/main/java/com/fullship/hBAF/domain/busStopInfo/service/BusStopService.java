package com.fullship.hBAF.domain.busStopInfo.service;

import com.fullship.hBAF.domain.busStopInfo.entity.BusStopInfo;
import com.fullship.hBAF.domain.busStopInfo.repository.BusStopRepository;
import com.fullship.hBAF.domain.busStopInfo.service.command.request.GetNearBusStopRequestCommand;
import com.fullship.hBAF.domain.busStopInfo.service.command.response.GetNearBusStopResponseCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusStopService {

    private final BusStopRepository busStopRepository;

    public GetNearBusStopResponseCommand getNearBusStop(GetNearBusStopRequestCommand command){
        double lng = Double.parseDouble(command.getLng());
        double lat = Double.parseDouble(command.getLat());

        List<BusStopInfo> list = new ArrayList<>();
        list = busStopRepository.findAllByRouteNo(command.getRouteNo());

        double min = 987654321;
        double minLng = 0;
        double minLat = 0;
        String busStopNo = "";
        String busStopName = "";
        for(BusStopInfo busStop : list){
            double diff = Math.abs(lng-Double.parseDouble(busStop.getBusStopLong()))-Math.abs(lat-Double.parseDouble(busStop.getBusStopLat()));
            if(min>diff){
                min = diff;
                busStopNo = busStop.getBusStopNo();
                busStopName = busStop.getBusStopName();
                minLng = Double.parseDouble(busStop.getBusStopLong());
                minLat = Double.parseDouble(busStop.getBusStopLat());
            }
        }
        GetNearBusStopResponseCommand responseCommand = GetNearBusStopResponseCommand.
                builder()
                .busStopNo(busStopNo)
                .busStopName(busStopName)
                .lng(String.valueOf(minLng))
                .lat(String.valueOf(minLat))
                .build();

        return responseCommand;
    }

}
