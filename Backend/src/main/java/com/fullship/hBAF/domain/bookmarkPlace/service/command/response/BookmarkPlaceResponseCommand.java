package com.fullship.hBAF.domain.bookmarkPlace.service.command.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "북마크 행동 설명")
public class BookmarkPlaceResponseCommand {
    @Schema(description = "북마크 설정 / 해제 설명")
    String response;
}
