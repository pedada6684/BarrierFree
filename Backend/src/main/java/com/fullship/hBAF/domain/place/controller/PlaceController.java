package com.fullship.hBAF.domain.place.controller;

import com.fullship.hBAF.domain.place.controller.request.CalculateAngleRequest;
import com.fullship.hBAF.domain.place.controller.request.GetPlaceListRequest;
import com.fullship.hBAF.domain.place.controller.request.PathSearchToTrafficRequest;
import com.fullship.hBAF.domain.place.controller.request.PathSearchToWheelRequest;
import com.fullship.hBAF.domain.place.controller.response.GetPlaceResponse;
import com.fullship.hBAF.domain.place.controller.response.PlaceListResponse;
import com.fullship.hBAF.domain.place.service.PlaceService;
import com.fullship.hBAF.domain.place.service.command.FindPathByAStarCommand;
import com.fullship.hBAF.domain.place.service.command.Request.AngleSlopeCommand;
import com.fullship.hBAF.domain.place.service.command.SearchPathToTrafficCommand;
import com.fullship.hBAF.domain.place.service.command.SearchPathToWheelCommand;
import com.fullship.hBAF.global.H3.service.H3IndexService;
import com.fullship.hBAF.global.api.response.OdSayPath;
import com.fullship.hBAF.global.api.response.PathGeoCode;
import com.fullship.hBAF.global.api.response.TaxiPathForm;
import com.fullship.hBAF.global.api.response.WheelPathForm;
import com.fullship.hBAF.global.api.service.command.OdSayPathCommand;
import com.fullship.hBAF.global.response.CommonResponseEntity;
import com.fullship.hBAF.global.response.SuccessCode;
import com.fullship.hBAF.util.H3;
import com.uber.h3core.exceptions.LineUndefinedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.fullship.hBAF.global.response.CommonResponseEntity.getResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
@Tag(name = "Place 컨트롤러", description = "장소 API 입니다.")
@Slf4j
public class PlaceController {

  private final PlaceService placeService;
  private final H3 h3;
  private final H3IndexService h3IndexService;

  @PostMapping("/path/wheel")
  @Operation(summary = "휠체어 도보 경로 조회", description = "계단이 없는 보행자 도보를 이용한 휠체어 이동 경로 조회")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WheelPathForm.class)))
  public ResponseEntity<CommonResponseEntity> searchPathByWheel(@RequestBody PathSearchToWheelRequest requestDto) {
    SearchPathToWheelCommand command = requestDto.createForWheel();
    WheelPathForm wheelPathForm = placeService.useWheelPath(command);

    List<WheelPathForm> response = new ArrayList<>();
    response.add(wheelPathForm);
    int[] se;
    if((se=placeService.findScarp(wheelPathForm.getGeoCode()))!=null){
      FindPathByAStarCommand findCommand = FindPathByAStarCommand.builder()
              .geoCodes(wheelPathForm.getGeoCode())
              .se(se)
              .type(requestDto.getType())
              .build();
      WheelPathForm pathByAStar = placeService.findPathByAStar(findCommand);
      response.add(pathByAStar);
    }
    return getResponseEntity(SuccessCode.OK, response);
  }

  @PostMapping("/path/transit")
  @Operation(summary = "대중교통 경로 조회", description = "대중교통을 이용하는 경로 조회")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = OdSayPath.class)))
  public ResponseEntity<CommonResponseEntity> searchPathByTransit(@RequestBody PathSearchToTrafficRequest requestDto) throws IOException, IOException {
    OdSayPathCommand command = requestDto.createForSearch();
    List<OdSayPath> odSayPaths = placeService.useTransitPath(command);
      return getResponseEntity(SuccessCode.OK, odSayPaths);
  }

  @PostMapping("/path/slope")
  @Operation(summary = "경사도 측정", description = "경로 내 휠체어 경로 경사도 측정")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = PathGeoCode.class)))
  public ResponseEntity<CommonResponseEntity> calculateAngleSlope(@RequestBody CalculateAngleRequest requestDto){
    AngleSlopeCommand command = requestDto.createForCalculate();
    return getResponseEntity(SuccessCode.OK, placeService.calculateAngle(command));
  }

  @PostMapping("/path/taxi")
  @Operation(summary = "택시 경로 조회", description = "택시를 이용하는 경로 조회")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TaxiPathForm.class)))
  public ResponseEntity<CommonResponseEntity> searchPathByTaxi(@RequestBody PathSearchToTrafficRequest requestDto) {
    SearchPathToTrafficCommand command = requestDto.createForTaxi();
    return getResponseEntity(SuccessCode.OK, placeService.useTaxiPath(command));
  }

  @PostMapping("/list")
  @Operation(summary = "장애 편의 시설 목록 불러오기", description = "장애 편의 시설 목록 불러오기")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = PlaceListResponse.class)))
  public ResponseEntity<CommonResponseEntity> getPlaceList(@RequestBody GetPlaceListRequest requestDto) {
    List<PlaceListResponse> placeList = placeService.getPlaceList(requestDto.toCommand());
    return getResponseEntity(SuccessCode.OK, placeList);
  }

  @GetMapping
  @Operation(summary = "장애 편의 시설 불러오기", description = "장애 편의 시설 불러오기")
  public ResponseEntity<GetPlaceResponse> getPlace(@RequestParam("poiId") String poiId){
    GetPlaceResponse response = placeService.getPlace(poiId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/test")
  @Operation(summary = "통신 테스트", description = "통신 테스트")
  public ResponseEntity<CommonResponseEntity> test() {
    System.out.println("통신 테스트");
    return getResponseEntity(SuccessCode.OK, "[0404-0315]테스트입니다.");
  }

  @GetMapping("/setH3")
  @Operation(summary = "H3 index 초기화 메서드", description = "H3 index 초기화 메서드")
  public ResponseEntity<CommonResponseEntity> setH3() throws LineUndefinedException, IOException {
    h3.setH3Index();
    return getResponseEntity(SuccessCode.OK, "H3 초기화 완료");
  }
}