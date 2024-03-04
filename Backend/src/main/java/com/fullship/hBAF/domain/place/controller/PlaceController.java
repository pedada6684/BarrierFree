package com.fullship.hBAF.domain.place.controller;

import com.fullship.hBAF.domain.place.service.PlaceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
@Tag(name = "Place 컨트롤러", description = "장소 API 입니다.")
public class PlaceController {
  private final PlaceService placeService;
}
