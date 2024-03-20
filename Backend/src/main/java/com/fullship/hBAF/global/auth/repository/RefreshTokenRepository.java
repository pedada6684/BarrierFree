package com.fullship.hBAF.global.auth.repository;

import com.fullship.hBAF.global.auth.entity.RedisRefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RedisRefreshToken, String> {
}
