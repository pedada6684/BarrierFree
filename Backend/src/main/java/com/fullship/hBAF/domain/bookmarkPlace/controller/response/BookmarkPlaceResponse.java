package com.fullship.hBAF.domain.bookmarkPlace.controller.response;

import com.fullship.hBAF.domain.bookmarkPlace.service.command.response.BookmarkPlaceResponseCommand;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookmarkPlaceResponse {

    BookmarkPlaceResponseCommand command;

}
