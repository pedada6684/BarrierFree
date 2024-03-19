package com.fullship.hBAF.domain.busStop.repository;

import com.fullship.hBAF.domain.busStop.entity.BusStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusStopRepository extends JpaRepository<BusStop,Long> {
    BusStop findBusStopByStopId(String stopId);

    BusStop findBusStopByArsId(String arsId);
}
