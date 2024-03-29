package com.fullship.hBAF.domain.bookmarkPlace.controller.response;

import com.fullship.hBAF.domain.bookmarkPlace.service.command.response.BookmarkPlaceResponseCommand;
import com.fullship.hBAF.domain.bookmarkPlace.service.command.response.GetBookmarkPlaceByMemberIdResponseCommand;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetBookmarkPlaceByMemberIdResponse {

    List<GetBookmarkPlaceByMemberIdResponseCommand> list;

}
