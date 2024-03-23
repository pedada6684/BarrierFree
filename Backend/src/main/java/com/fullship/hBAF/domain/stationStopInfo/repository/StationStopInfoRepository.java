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

  @Query("SELECT s FROM StationStopInfo s WHERE s.arrTime > :futureTime And s.metroInfoId in :metroList")
  List<StationStopInfo> findByTimeAndMetroInfo(@Param("futureTime") String futureTime, @Param("metroList") List<Integer> metroIdList);

}
