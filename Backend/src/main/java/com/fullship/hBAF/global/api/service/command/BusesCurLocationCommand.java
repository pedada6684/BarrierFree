package com.fullship.hBAF.global.api.service.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusesCurLocationCommand {

    String busNodeId;
    String dir;
    String license;

}
