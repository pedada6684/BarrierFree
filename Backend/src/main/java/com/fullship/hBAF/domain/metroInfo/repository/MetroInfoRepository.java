package com.fullship.hBAF.domain.metroInfo.repository;

import com.fullship.hBAF.domain.metroInfo.entity.MetroInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MetroInfoRepository  extends JpaRepository<MetroInfo,Long> {

  @Query("SELECT m.id FROM MetroInfo m WHERE m.isWeekday = :isWeekDay AND mod(m.id, 2) = 0")
  List<Integer> findAllByIsWeekDayAndUp(@Param("isWeekDay") Boolean isWeekDay);

  @Query("SELECT m.id FROM MetroInfo m WHERE m.isWeekday = :isWeekDay AND mod(m.id, 2) = 1")
  List<Integer> findAllByIsWeekDayAndDown(@Param("isWeekDay") Boolean isWeekDay);
}
