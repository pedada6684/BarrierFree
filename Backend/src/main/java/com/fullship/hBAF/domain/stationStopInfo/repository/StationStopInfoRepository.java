package com.fullship.hBAF.domain.stationStopInfo.repository;

import com.fullship.hBAF.domain.stationStopInfo.entity.StationStopInfo;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StationStopInfoRepository extends JpaRepository<StationStopInfo,Long> {

  @Query("SELECT s.metroInfoId FROM StationStopInfo s WHERE s.subwayInfoId = :startSubway AND s.arrTime > :curTime AND s.metroInfoId in :metroList order by s.metroInfoId ")
  List<Integer> findTime(@Param("startSubway") int startSubway, @Param("curTime") String curTime, @Param("metroList") List<Integer> metroList);

  @Query("SELECT s.subwayInfoId from StationStopInfo s where s.metroInfoId = :metro and s.arrTime < :curTime order by s.subwayInfoId DESC")
  List<Integer> findCurPos(Integer metro, String curTime);
}
