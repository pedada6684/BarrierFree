package com.fullship.hBAF.domain.member.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProfileResponse {
    String profileImgUrl;
}
