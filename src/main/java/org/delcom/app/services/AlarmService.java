package org.delcom.app.services;

import org.delcom.app.dto.AlarmDto;
import org.delcom.app.entities.Alarm;
import org.delcom.app.repositories.AlarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlarmService {
    
    @Autowired
    private AlarmRepository alarmRepository;
    
    public List<AlarmDto> getAllAlarms() {
        return alarmRepository.findAllByOrderByAlarmTimeAsc()
                .stream()
                .map(AlarmDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<AlarmDto> getActiveAlarms() {
        return alarmRepository.findByActiveTrueOrderByAlarmTimeAsc()
                .stream()
                .map(AlarmDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public AlarmDto saveAlarm(AlarmDto alarmDto) {
        Alarm alarm = alarmDto.toEntity();
        Alarm savedAlarm = alarmRepository.save(alarm);
        return AlarmDto.fromEntity(savedAlarm);
    }
    
    public void deleteAlarm(Long id) {
        alarmRepository.deleteById(id);
    }
    
    public AlarmDto toggleAlarm(Long id) {
        Alarm alarm = alarmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alarm not found"));
        alarm.setActive(!alarm.isActive());
        Alarm savedAlarm = alarmRepository.save(alarm);
        return AlarmDto.fromEntity(savedAlarm);
    }
    
    public List<AlarmDto> getAlarmsTriggeringNow() {
        LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
        return alarmRepository.findActiveAlarmsByTime(currentTime)
                .stream()
                .map(AlarmDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public AlarmDto getAlarmById(Long id) {
        Alarm alarm = alarmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alarm not found"));
        return AlarmDto.fromEntity(alarm);
    }
}