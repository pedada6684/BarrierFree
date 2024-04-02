package com.fullship.hBAF.domain.bookmarkPlace.service;

import com.fullship.hBAF.domain.bookmarkPlace.controller.response.BookmarkPlaceResponse;
import com.fullship.hBAF.domain.bookmarkPlace.controller.response.GetBookmarkPlaceByMemberIdResponse;
import com.fullship.hBAF.domain.bookmarkPlace.entity.BookmarkPlace;
import com.fullship.hBAF.domain.bookmarkPlace.repository.BookmarkPlaceRepository;
import com.fullship.hBAF.domain.bookmarkPlace.service.command.request.BookmarkPlaceRequestCommand;
import com.fullship.hBAF.domain.bookmarkPlace.service.command.response.BookmarkPlaceResponseCommand;
import com.fullship.hBAF.domain.bookmarkPlace.service.command.response.GetBookmarkPlaceByMemberIdResponseCommand;
import com.fullship.hBAF.domain.member.entity.Member;
import com.fullship.hBAF.domain.member.repository.MemberRepository;
import com.fullship.hBAF.domain.place.entity.Place;
import com.fullship.hBAF.domain.place.repository.PlaceRepository;
import com.fullship.hBAF.domain.place.service.PlaceService;
import com.fullship.hBAF.domain.place.service.command.CreatePlaceCommand;
import com.fullship.hBAF.global.api.response.KakaoPlace;
import com.fullship.hBAF.global.api.service.KakaoMapApiService;
import com.fullship.hBAF.global.api.service.command.SearchKakaoPlaceCommand;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkPlaceService {

  private final BookmarkPlaceRepository bookmarkPlaceRepository;
  private final MemberRepository memberRepository;
  private final PlaceRepository placeRepository;
  private final PlaceService placeService;
  private final KakaoMapApiService kakaoMapApiService;

  public GetBookmarkPlaceByMemberIdResponse getBookmarkPlaceByMember(Long memberId){

    Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));
    List<BookmarkPlace> list = bookmarkPlaceRepository.findAllByMemberId(memberId);

    List<GetBookmarkPlaceByMemberIdResponseCommand> commandList = new ArrayList<>();

    for(BookmarkPlace place : list){
      Place placeByPoiId = placeRepository.findPlaceByPoiId(place.getPoiId());
      GetBookmarkPlaceByMemberIdResponseCommand command = GetBookmarkPlaceByMemberIdResponseCommand.from(placeByPoiId);
      commandList.add(command);
    }

    GetBookmarkPlaceByMemberIdResponse response = GetBookmarkPlaceByMemberIdResponse.builder().list(commandList).build();
    return response;
  }

  public BookmarkPlaceResponse bookmark(BookmarkPlaceRequestCommand command){
    if(command.getMemberId()==null || command.getPoiId()==null)
      throw new CustomException(ErrorCode.REQUEST_NOT_FOUND);

    Member member = memberRepository.getReferenceById(command.getMemberId());
    BookmarkPlace bookmarkPlace;

    BookmarkPlaceResponseCommand responseCommand = null;
    if((bookmarkPlace=bookmarkPlaceRepository.findBookmarkPlaceByMemberAndPoiId(member,command.getPoiId()))==null) {
      bookmarkPlace = BookmarkPlace.createTobookmarkPlace(member, command.getPoiId());
      bookmarkPlaceRepository.save(bookmarkPlace);

      responseCommand = BookmarkPlaceResponseCommand.builder()
              .response("bookmark")
              .build();

      if(placeRepository.findPlaceByPoiId(command.getPoiId())==null) {
        //place 생성
        SearchKakaoPlaceCommand searchCommand = SearchKakaoPlaceCommand.builder()
                .lng(command.getLongitude())
                .lat(command.getLatitude())
                .keyword(command.getPlaceName())
                .build();
        KakaoPlace kakaoPlace = kakaoMapApiService.getKakaoPlace(searchCommand);
        if (kakaoPlace == null) {
          throw new CustomException(ErrorCode.NO_AVAILABLE_API);
        }else{
          CreatePlaceCommand createPlaceCommand = CreatePlaceCommand.fromKakaoPlace(kakaoPlace);
          placeService.createPlace(createPlaceCommand);
        }
      }
    }
    else {
      bookmarkPlaceRepository.delete(bookmarkPlace);
      responseCommand = BookmarkPlaceResponseCommand.builder()
              .response("unBookmark")
              .build();
    }
    return BookmarkPlaceResponse.builder().command(responseCommand).build();
  }
}
