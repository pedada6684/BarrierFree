package com.fullship.hBAF.global.H3.service;

import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class H3IndexService {
    @Resource(name = "redisTemplateForH3")
    private HashOperations<String, Long, Double> hashOperations;
    private final String H3IndexKey = "H3INDEX";
    /**
     * H3 index를 redis에 삽입
     * @param h3IndexSet: 삽입할 indexSet
     */
    public void saveH3IndexSet(Map<Long,Double> h3IndexSet){
        hashOperations.putAll(H3IndexKey, h3IndexSet);
    }

    /**
     * 해당 셀의 고도값 조회
     * @param index
     * @return
     */
    public Double getAltitude(Long index){
        Double altitude = hashOperations.get(H3IndexKey, index);
        if (altitude == null){
            throw new CustomException(ErrorCode.H3_INDEX_NOT_FOUND);
        }
        return altitude;
    }

    /**
     * redis에 저장된 H3index 맴버인지 확인
     * @param index: 확인할 index
     * @return true/false
     */
    public boolean isContainInRedisH3(Long index){
        return hashOperations.hasKey(H3IndexKey, index);
    }

    public Long getH3IdexSize(){
        return hashOperations.size(H3IndexKey);
    }
}
