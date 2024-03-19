package com.fullship.hBAF.domain.busStopInfo.service.command.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CountStopRequestCommand {
  String routeNo;
  String curStopNo;
  String objStopNo;
}
