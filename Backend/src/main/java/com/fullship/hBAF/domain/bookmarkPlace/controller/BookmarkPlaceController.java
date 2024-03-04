package com.fullship.hBAF.domain.bookmarkPlace.controller;

import com.fullship.hBAF.domain.bookmarkPlace.service.BookmarkPlaceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bmplace")
@Tag(name = "BookmarkPlace 컨트롤러", description = "장소 즐겨찾기 API 입니다.")
public class BookmarkPlaceController {

  private final BookmarkPlaceService bookmarkPlaceService;
}
