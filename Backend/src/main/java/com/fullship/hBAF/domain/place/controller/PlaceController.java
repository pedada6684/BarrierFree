package com.fullship.hBAF.domain.place.controller;

import static com.fullship.hBAF.global.response.CommonResponseEntity.getResponseEntity;

import com.fullship.hBAF.domain.place.controller.request.PathSearchToTrafficRequest;
import com.fullship.hBAF.domain.place.controller.request.PathSearchToWheelRequest;
import com.fullship.hBAF.domain.place.controller.response.PlaceListResonse;
import com.fullship.hBAF.domain.place.controller.response.PlaceResponse;
import com.fullship.hBAF.domain.place.service.PlaceService;
import com.fullship.hBAF.global.api.service.OdSayApiService;
import com.fullship.hBAF.global.api.service.TMapApiService;
import com.fullship.hBAF.global.api.service.command.OdSayPathCommand;
import com.fullship.hBAF.global.api.service.command.SearchPathToTrafficCommand;
import com.fullship.hBAF.global.api.service.command.SearchPathToWheelCommand;
import com.fullship.hBAF.global.response.CommonResponseEntity;
import com.fullship.hBAF.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
@Tag(name = "Place 컨트롤러", description = "장소 API 입니다.")
@Slf4j
public class PlaceController {

  private final PlaceService placeService;
  private final TMapApiService tMapApiService;
  private final OdSayApiService odSayApiService;

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
  public ResponseEntity<?> searchPathByTransit(@RequestBody PathSearchToTrafficRequest requestDto) {
    OdSayPathCommand command = requestDto.createForSearch();
    return getResponseEntity(SuccessCode.OK, placeService.useTransitPath(command));
  }

  @PostMapping("/path/taxi")
  @Operation(summary = "택시 경로 조회", description = "택시를 이용하는 경로 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "경로 조회 성공", content = @Content(schema = @Schema(implementation = String.class))),
      @ApiResponse(responseCode = "400", description = "경로 조회 실패")
  })
  public ResponseEntity<?> searchPathByTaxi(@RequestBody PathSearchToTrafficRequest requestDto) {
    SearchPathToTrafficCommand command = requestDto.createForTaxi();
    return getResponseEntity(SuccessCode.OK, tMapApiService.searchPathToCar(command));
  }

  @GetMapping("/list")
  @Operation(summary = "장애 편의 시설 목록 불러오기", description = "장애 편의 시설 목록 불러오기")
  public ResponseEntity<CommonResponseEntity> getPlaceListByCategory(@RequestParam("category") String category) {
    log.info("장애 편의 시설 카테고리별 불러오기 - 카테고리 : {}", category);
    List<PlaceListResonse> placeList = placeService.getPlaceListByCategory(category);
    return getResponseEntity(SuccessCode.OK, placeList);
  }

  @GetMapping()
  @Operation(summary = "장애 편의 시설 상세 정보 불러오기", description = "장애 편의 시설 상세 정보 불러오기")
  public ResponseEntity<CommonResponseEntity> getPlaceDetail(@RequestParam("placeId") Long placeId) {
    log.info("장애 편의 시설 상세 정보 불러오기 - 시설 id : {}", placeId);
    PlaceResponse place = placeService.getPlaceDetail(placeId);
    return getResponseEntity(SuccessCode.OK, place);
  }

  @GetMapping("/test")
  @Operation(summary = "통신 테스트", description = "통신 테스트")
  public ResponseEntity<CommonResponseEntity> test()
          throws ParseException {
    System.out.println("통신 테스트");
    return getResponseEntity(SuccessCode.OK, "테스트입니다.");
  }
}