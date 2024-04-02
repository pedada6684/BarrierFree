package com.fullship.hBAF.domain.busStop.repository;

import com.fullship.hBAF.domain.busStop.entity.BusStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusStopRepository extends JpaRepository<BusStop, Long> {

  BusStop findBusStopByStopIdAndBusId(String startStationId, String busId);

  BusStop findBusStopByLocalStationIdAndBusId(String localStationId, String busId);
}
