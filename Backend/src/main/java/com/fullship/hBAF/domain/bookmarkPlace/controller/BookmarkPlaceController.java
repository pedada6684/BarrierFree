package com.fullship.hBAF.domain.bookmarkPlace.controller;

import com.fullship.hBAF.domain.bookmarkPlace.controller.request.BookmarkPlaceRequest;
import com.fullship.hBAF.domain.bookmarkPlace.controller.response.BookmarkPlaceResponse;
import com.fullship.hBAF.domain.bookmarkPlace.service.BookmarkPlaceService;
import com.fullship.hBAF.domain.bookmarkPlace.service.command.request.BookmarkPlaceRequestCommand;
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
  public ResponseEntity<BookmarkPlaceResponse> bookmarkPlace(@RequestBody BookmarkPlaceRequest request){

    BookmarkPlaceRequestCommand command = BookmarkPlaceRequestCommand.builder()
            .memberId(request.getMemberId())
            .placeName(request.getPlaceName())
            .address(request.getAddress())
            .longitude(request.getLongitude())
            .latitude(request.getLatitude())
            .build();

    BookmarkPlaceResponse response = bookmarkPlaceService.bookmark(command);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
