package org.delcom.app.repositories;

import org.delcom.app.entities.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    
    List<Alarm> findByActiveTrueOrderByAlarmTimeAsc();
    
    @Query("SELECT a FROM Alarm a WHERE a.active = true AND a.alarmTime = :alarmTime")
    List<Alarm> findActiveAlarmsByTime(LocalTime alarmTime);
    
    List<Alarm> findAllByOrderByAlarmTimeAsc();
}