package com.fullship.hBAF.global.auth.controller.response;

import com.fullship.hBAF.global.auth.jwt.AccessToken;
import com.fullship.hBAF.global.auth.jwt.RefreshToken;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResult {
    AccessToken accessToken;
    RefreshToken refreshToken;
}
