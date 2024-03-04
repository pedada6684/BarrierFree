package com.fullship.hBAF.domain.bookmarkPlace.service;

import com.fullship.hBAF.domain.bookmarkPlace.repository.BookmarkPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkPlaceService {

  private final BookmarkPlaceRepository bookmarkPlaceRepository;
}
