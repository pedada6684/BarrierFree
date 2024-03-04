package com.fullship.hBAF.domain.bookmarkRoute.controller;

import com.fullship.hBAF.domain.bookmarkRoute.service.BookmarkRouteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bmroute")
@Tag(name = "BookmarkRoute 컨트롤러", description = "경로 즐겨찾기 API 입니다.")
public class BookmarkRouteController {
  private final BookmarkRouteService bookmarkRouteService;
}
