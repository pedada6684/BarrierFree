package com.fullship.hBAF.domain.bookmarkPlace.controller;

import com.fullship.hBAF.domain.bookmarkPlace.controller.request.BookmarkPlaceRequest;
import com.fullship.hBAF.domain.bookmarkPlace.controller.response.GetBookmarkPlaceByMemberIdResponse;
import com.fullship.hBAF.domain.bookmarkPlace.controller.response.BookmarkPlaceResponse;
import com.fullship.hBAF.domain.bookmarkPlace.service.BookmarkPlaceService;
import com.fullship.hBAF.domain.bookmarkPlace.service.command.request.BookmarkPlaceRequestCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bmplace")
@Tag(name = "BookmarkPlace 컨트롤러", description = "장소 즐겨찾기 API 입니다.")
public class BookmarkPlaceController {

  private final BookmarkPlaceService bookmarkPlaceService;

  @PostMapping
  @Operation(summary = "북마크 설정 / 해제", description = "poiId를 이용한 북마크 설정 해제")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = BookmarkPlaceResponse.class)))
  public ResponseEntity<BookmarkPlaceResponse> bookmarkPlace(@RequestBody BookmarkPlaceRequest request) {

    BookmarkPlaceRequestCommand command = BookmarkPlaceRequestCommand.builder()
        .memberId(request.getMemberId())
        .poiId(request.getPoiId())
        .placeName(request.getPlaceName())
        .address(request.getAddress())
        .longitude(request.getLongitude())
        .latitude(request.getLatitude())
        .build();

    BookmarkPlaceResponse response = bookmarkPlaceService.bookmark(command);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping
  @Operation(summary = "유저 별 북마크 조회", description = "memberId를 이용한 북마크 조회")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = GetBookmarkPlaceByMemberIdResponse.class)))
  public ResponseEntity<GetBookmarkPlaceByMemberIdResponse> getBookmarkPlaceByMemberId(@RequestParam("memberId") Long memberId){

    GetBookmarkPlaceByMemberIdResponse response = bookmarkPlaceService.getBookmarkPlaceByMember(memberId);

    return new ResponseEntity<>(response,HttpStatus.OK);
  }
}
