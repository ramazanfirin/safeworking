package com.masterteknoloji.net.repository;

import com.masterteknoloji.net.domain.Alarm;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.ZonedDateTime;
import java.util.List;

import com.masterteknoloji.net.domain.enumeration.AlarmType;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the Alarm entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    
    Page<Alarm> findByPersonIdAndInsertDateGreaterThanEqual(Long personId, ZonedDateTime insertDate, Pageable pageable);
    
    Page<Alarm> findByPersonId(Long personId, Pageable pageable);
    
    Page<Alarm> findByInsertDateGreaterThanEqual(ZonedDateTime insertDate, Pageable pageable);
    
    Page<Alarm> findByPersonIdAndInsertDateBetween(Long personId, ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable);
    
    Page<Alarm> findByInsertDateBetween(ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable);

    @Query("SELECT a FROM Alarm a WHERE " +
           "(:personId IS NULL OR a.person.id = :personId) AND " +
           "(:alarmType IS NULL OR a.alarmType = :alarmType) AND " +
           "(:falseAlarm IS NULL OR a.falseAlarm = :falseAlarm) AND " +
           "(:processed IS NULL OR a.processed = :processed) AND " +
           "(:startDate IS NULL OR a.insertDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.insertDate <= :endDate)")
    Page<Alarm> searchAlarms(
            @Param("personId") Long personId,
            @Param("alarmType") AlarmType alarmType,
            @Param("falseAlarm") Boolean falseAlarm,
            @Param("processed") Boolean processed,
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate,
            Pageable pageable);
}
