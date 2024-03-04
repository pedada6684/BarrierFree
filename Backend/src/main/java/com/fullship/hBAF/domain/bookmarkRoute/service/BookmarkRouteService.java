package com.fullship.hBAF.domain.bookmarkRoute.service;

import com.fullship.hBAF.domain.bookmarkRoute.repository.BookmarkRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkRouteService {

  private final BookmarkRouteRepository bookmarkRouteRepository;
}
