package com.fullship.hBAF.domain.place.controller;

import static com.fullship.hBAF.global.response.CommonResponseEntity.getResponseEntity;

import com.fullship.hBAF.domain.place.controller.request.CalculateAngleRequest;
import com.fullship.hBAF.domain.place.controller.request.GetPlaceListRequest;
import com.fullship.hBAF.domain.place.controller.request.PathSearchToTrafficRequest;
import com.fullship.hBAF.domain.place.controller.request.PathSearchToWheelRequest;
import com.fullship.hBAF.domain.place.controller.response.PlaceListResponse;
import com.fullship.hBAF.domain.place.controller.response.PlaceResponse;
import com.fullship.hBAF.domain.place.service.PlaceService;
import com.fullship.hBAF.domain.place.service.command.Request.AngleSlopeCommand;
import com.fullship.hBAF.global.api.response.*;
import com.fullship.hBAF.domain.place.service.command.Request.GetPlaceListRequestComment;
import com.fullship.hBAF.global.api.service.DataApiService;
import com.fullship.hBAF.global.api.service.TMapApiService;
import com.fullship.hBAF.global.api.service.TagoApiService;
import com.fullship.hBAF.global.api.service.command.OdSayPathCommand;
import com.fullship.hBAF.global.api.service.command.SearchPathToTrafficCommand;
import com.fullship.hBAF.global.api.service.command.SearchPathToWheelCommand;
import com.fullship.hBAF.global.response.CommonResponseEntity;
import com.fullship.hBAF.global.response.SuccessCode;
import com.fullship.hBAF.util.BarrierFreeConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
@Tag(name = "Place 컨트롤러", description = "장소 API 입니다.")
@Slf4j
public class PlaceController {

  private final PlaceService placeService;
  private final TMapApiService tMapApiService;
  private final TagoApiService tagoApiService;
  private final BarrierFreeConstructor barrierFreeConstructor;
  private final DataApiService dataApiService;

  @PostMapping("/path/wheel")
  @Operation(summary = "휠체어 도보 경로 조회", description = "계단이 없는 보행자 도보를 이용한 휠체어 이동 경로 조회")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WheelPathForm.class)))
  public ResponseEntity<CommonResponseEntity> searchPathByWheel(@RequestBody PathSearchToWheelRequest requestDto) {
    SearchPathToWheelCommand command = requestDto.createForWheel();
    return getResponseEntity(SuccessCode.OK, placeService.useWheelPath(command));
  }

  @PostMapping("/path/transit")
  @Operation(summary = "대중교통 경로 조회", description = "대중교통을 이용하는 경로 조회")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = OdSayPath.class)))
  public ResponseEntity<CommonResponseEntity> searchPathByTransit(@RequestBody PathSearchToTrafficRequest requestDto) {
    OdSayPathCommand command = requestDto.createForSearch();
    return getResponseEntity(SuccessCode.OK, placeService.useTransitPath(command));
  }

  @PostMapping("/path/slope")
  @Operation(summary = "경사도 측정", description = "경로 내 휠체어 경로 경사도 측정")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = PathGeoCode.class)))
  public ResponseEntity<CommonResponseEntity> calculateAngleSlope(@RequestBody CalculateAngleRequest requestDto){
    log.info("PathGeoCodes = {}", requestDto.getPathGeoCodes());
    AngleSlopeCommand command = requestDto.createForCalculate();
    return getResponseEntity(SuccessCode.OK, placeService.calculateAngle(command));
  }

  @PostMapping("/path/taxi")
  @Operation(summary = "택시 경로 조회", description = "택시를 이용하는 경로 조회")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TaxiPathForm.class)))
  public ResponseEntity<CommonResponseEntity> searchPathByTaxi(@RequestBody PathSearchToTrafficRequest requestDto) {
    SearchPathToTrafficCommand command = requestDto.createForTaxi();
    return getResponseEntity(SuccessCode.OK, tMapApiService.searchPathToCar(command));
  }

  @PostMapping("/list")
  @Operation(summary = "장애 편의 시설 목록 불러오기", description = "장애 편의 시설 목록 불러오기")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = PlaceListResponse.class)))
  public ResponseEntity<CommonResponseEntity> getPlaceList(@RequestBody GetPlaceListRequest request) {
    List<PlaceListResponse> placeList = placeService.getPlaceList(
        GetPlaceListRequestComment.builder().lat(request.getLat()).lng(request.getLng()).build());
    return getResponseEntity(SuccessCode.OK, placeList);
  }

  @GetMapping("/test")
  @Operation(summary = "통신 테스트", description = "통신 테스트")
  public ResponseEntity<CommonResponseEntity> test() throws ParseException {
    System.out.println("통신 테스트");
    return getResponseEntity(SuccessCode.OK, "테스트입니다.");
  }

  @GetMapping("/tago-test")
  @Operation(summary = "tago 테스트", description = "통신 테스트")
  public ResponseEntity<CommonResponseEntity> tagoTest() throws ParseException {
    System.out.println("tago 테스트");
    List<BusesCurLocation> busesByPublicId = tagoApiService.findBusesByPublicId("30300108", "2");
    return getResponseEntity(SuccessCode.OK, busesByPublicId);
  }

  @GetMapping("/api-test")
  @Operation(summary = "tago 테스트", description = "통신 테스트")
  public ResponseEntity<CommonResponseEntity> apiTest() throws ParseException, IOException, ParserConfigurationException, SAXException {
    List<Map<String, String>> maps = barrierFreeConstructor.searchBarrierFreePlace();
    return getResponseEntity(SuccessCode.OK, maps);
  }

  @GetMapping("/api-test2")
  @Operation(summary = "openapi 테스트", description = "통신 테스트")
  public ResponseEntity<CommonResponseEntity> openapi() throws ParseException, IOException, ParserConfigurationException, SAXException {
    return getResponseEntity(SuccessCode.OK, null);
  }
}