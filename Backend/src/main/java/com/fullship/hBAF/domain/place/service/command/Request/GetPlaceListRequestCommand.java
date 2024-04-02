package com.fullship.hBAF.domain.place.service.command.Request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Data
@Builder
public class GetPlaceListRequestCommand {
    Double lat;
    Double lng;
}
