package com.fullship.hBAF.domain.place.service.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePlaceCommand {
    private String placeName;
    private String address;
    private String latitude;
    private String longitude;
    private String poiId;
    private String category;
    private String barrierFree;
    private String wtcltId;
    private Boolean type;
}
