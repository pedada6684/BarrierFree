package com.fullship.hBAF.domain.stationInfo.repository;

import com.fullship.hBAF.domain.stationInfo.entity.StationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationInfoRepository extends JpaRepository<StationInfo,Long> {

  StationInfo findStationInfoBySubwayName(String startName);
}
