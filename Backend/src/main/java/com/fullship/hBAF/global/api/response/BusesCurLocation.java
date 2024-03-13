package com.fullship.hBAF.global.api.response;

import com.fullship.hBAF.global.api.service.command.BusesCurLocationCommand;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BusesCurLocation {

    List<BusesCurLocationCommand> list;

}
