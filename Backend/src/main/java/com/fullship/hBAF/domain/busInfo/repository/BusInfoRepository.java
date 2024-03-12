package com.fullship.hBAF.domain.busInfo.repository;

import com.fullship.hBAF.domain.busInfo.entity.BusInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusInfoRepository extends JpaRepository<BusInfo,Long> {

    BusInfo findBusInfoByLicense(String license);

}
