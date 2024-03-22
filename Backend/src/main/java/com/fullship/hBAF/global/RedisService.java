package com.fullship.hBAF.global;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {
    private final String H3IndexKey = "H3INDEXSET";
    private final RedisTemplate<String, Long> redisTemplate;
    @Resource(name = "redisTemplate")
    private SetOperations<String, Long> setOperations;
    /**
     * H3 index를 redis에 삽입
     * @param h3IndexSet: 삽입할 indexSet
     */
    @Transactional
    public void saveH3IndexSet(Set<Long> h3IndexSet){
        for (Long h3Index : h3IndexSet) {
            setOperations.add(H3IndexKey, h3Index);
        }
    }

    /**
     * redis에 저장된 H3index 맴버인지 확인
     * @param findH3Index: 확인할 index
     * @return true/false
     */
    public boolean isContainInRedisH3(Long findH3Index){
        return Boolean.TRUE.equals(setOperations.isMember(H3IndexKey, findH3Index));
    }
}
