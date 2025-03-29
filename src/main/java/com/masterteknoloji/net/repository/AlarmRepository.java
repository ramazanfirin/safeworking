package com.masterteknoloji.net.repository;

import com.masterteknoloji.net.domain.Alarm;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.ZonedDateTime;

/**
 * Spring Data JPA repository for the Alarm entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    
    Page<Alarm> findByPersonIdAndInsertDateGreaterThanEqual(Long personId, ZonedDateTime insertDate, Pageable pageable);
    
    Page<Alarm> findByPersonId(Long personId, Pageable pageable);
    
    Page<Alarm> findByInsertDateGreaterThanEqual(ZonedDateTime insertDate, Pageable pageable);
}
