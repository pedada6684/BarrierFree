package com.fullship.hBAF.domain.metroInfo.repository;

import com.fullship.hBAF.domain.metroInfo.entity.MetroInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetroInfoRepository  extends JpaRepository<MetroInfo,Long> {

}
