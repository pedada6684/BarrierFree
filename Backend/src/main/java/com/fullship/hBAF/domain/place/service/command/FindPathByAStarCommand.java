package com.fullship.hBAF.domain.place.service.command;

import com.fullship.hBAF.global.api.response.GeoCode;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class FindPathByAStarCommand implements Serializable {
    List<GeoCode> geoCodes;
    int[] se;
    String type;
}
