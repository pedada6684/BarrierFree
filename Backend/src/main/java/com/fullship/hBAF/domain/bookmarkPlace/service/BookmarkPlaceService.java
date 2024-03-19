package com.fullship.hBAF.domain.bookmarkPlace.service;

import com.fullship.hBAF.domain.bookmarkPlace.controller.response.BookmarkPlaceResponse;
import com.fullship.hBAF.domain.bookmarkPlace.entity.BookmarkPlace;
import com.fullship.hBAF.domain.bookmarkPlace.repository.BookmarkPlaceRepository;
import com.fullship.hBAF.domain.bookmarkPlace.service.command.request.BookmarkPlaceRequestCommand;
import com.fullship.hBAF.domain.bookmarkPlace.service.command.response.BookmarkPlaceResponseCommand;
import com.fullship.hBAF.domain.member.entity.Member;
import com.fullship.hBAF.domain.member.repository.MemberRepository;
import com.fullship.hBAF.domain.place.entity.Place;
import com.fullship.hBAF.domain.place.repository.PlaceRepository;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkPlaceService {

  private final BookmarkPlaceRepository bookmarkPlaceRepository;
  private final MemberRepository memberRepository;
  private final PlaceRepository placeRepository;

  public BookmarkPlaceResponse bookmark(BookmarkPlaceRequestCommand command){
    if(command.getMemberId()==null || command.getPoiId()==null)
      throw new CustomException(ErrorCode.REQUEST_NOT_FOUND);

    Member member = memberRepository.getReferenceById(command.getMemberId());

    BookmarkPlace bookmarkPlace;
    if((bookmarkPlace=bookmarkPlaceRepository.findBookmarkPlaceByMemberAndPoiId(member,command.getPoiId()))==null) {

      bookmarkPlace = BookmarkPlace.createTobookmarkPlace(member, command.getPoiId());
      bookmarkPlaceRepository.save(bookmarkPlace);

      if(placeRepository.findPlaceByPoiId(command.getPoiId())!=null) {
        Place place = Place.createNewPlace(
                command.getPlaceName(),
                command.getAddress(),
                command.getLatitude(),
                command.getLongitude(),
                command.getPoiId(),
                "",
                "",
                "",
                0L
        );
        placeRepository.save(place);
        BookmarkPlaceResponseCommand responseCommand = BookmarkPlaceResponseCommand.builder()
                .response("bookmark")
                .build();
      }
    }
    else
      bookmarkPlaceRepository.delete(bookmarkPlace);

    BookmarkPlaceResponseCommand responseCommand = BookmarkPlaceResponseCommand.builder()
            .response("unBookmark")
            .build();
    return BookmarkPlaceResponse.builder().command(responseCommand).build();
  }
}
