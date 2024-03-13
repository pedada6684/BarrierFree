package com.fullship.hBAF.domain.busStopInfo.repository;

import com.fullship.hBAF.domain.busStopInfo.entity.BusStopInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusStopRepository extends JpaRepository<BusStopInfo,Long> {

}
