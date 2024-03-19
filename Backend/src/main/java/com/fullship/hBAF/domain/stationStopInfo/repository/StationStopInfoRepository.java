package com.fullship.hBAF.domain.stationStopInfo.repository;

import com.fullship.hBAF.domain.stationStopInfo.entity.StationStopInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationStopInfoRepository extends JpaRepository<StationStopInfo,Long> {

}
