package com.fullship.hBAF.domain.place.controller;

import static com.fullship.hBAF.global.response.CommonResponseEntity.getResponseEntity;

import com.fullship.hBAF.domain.place.controller.request.PathSearchToTransitRequest;
import com.fullship.hBAF.domain.place.controller.request.PathSearchToWheelRequest;
import com.fullship.hBAF.domain.place.service.PlaceService;
import com.fullship.hBAF.global.api.service.TMapApiService;
import com.fullship.hBAF.global.api.service.command.SearchPathToTransitCommand;
import com.fullship.hBAF.global.api.service.command.SearchPathToWheelCommand;
import com.fullship.hBAF.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
@Tag(name = "Place 컨트롤러", description = "장소 API 입니다.")
public class PlaceController {

  private final PlaceService placeService;
  private final TMapApiService tMapApiService;

  @PostMapping("/path/wheel")
  @Operation(summary = "휠체어 도보 경로 조회", description = "계단이 없는 보행자 도보를 이용한 휠체어 이동 경로 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "경로 조회 성공", content = @Content(schema = @Schema(implementation = String.class))),
      @ApiResponse(responseCode = "400", description = "경로 조회 실패")
  })
  public ResponseEntity<?> searchPathByWheel(@RequestBody PathSearchToWheelRequest requestDto) {
    SearchPathToWheelCommand command = requestDto.createForWheel();
    return getResponseEntity(SuccessCode.OK, tMapApiService.searchPathToWheel(command));
  }

  @PostMapping("/path/transit")
  @Operation(summary = "대중교통 경로 조회", description = "대중교통을 이용하는 경로 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "경로 조회 성공", content = @Content(schema = @Schema(implementation = String.class))),
      @ApiResponse(responseCode = "400", description = "경로 조회 실패")
  })
  public ResponseEntity<?> searchPathByTransit(@RequestBody PathSearchToTransitRequest requestDto) {
    SearchPathToTransitCommand command = requestDto.createForTransit();
    return getResponseEntity(SuccessCode.OK, tMapApiService.searchPathToTransit(command));
//    return getResponseEntity(SuccessCode.OK, placeService.useTransitPath(command));
  }
}