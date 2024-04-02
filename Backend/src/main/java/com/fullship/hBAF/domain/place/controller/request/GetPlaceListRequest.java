package com.fullship.hBAF.domain.place.controller.request;

import com.fullship.hBAF.domain.place.service.command.Request.GetPlaceListRequestCommand;
import lombok.Getter;

import java.text.DecimalFormat;

@Getter
public class GetPlaceListRequest {

    String lat;
    String lng;
    public GetPlaceListRequestCommand toCommand(){
        double dlat = Double.parseDouble(lat);
        double dlng = Double.parseDouble(lng);
        DecimalFormat df = new DecimalFormat("#.####");

        return GetPlaceListRequestCommand.builder()
                .lat(Double.parseDouble(df.format(dlat)))
                .lng(Double.parseDouble(df.format(dlng)))
                .build();
    }

}
