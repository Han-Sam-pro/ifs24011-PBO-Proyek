package org.delcom.app.dto;

import org.delcom.app.entities.Alarm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class AlarmDto {
    
    private Long id;
    
    @NotNull(message = "Waktu alarm tidak boleh kosong")
    private LocalTime alarmTime;
    
    @NotBlank(message = "Label alarm tidak boleh kosong")
    private String label;
    
    private boolean active = true;
    
    private String soundFile = "/sound/Yo-Phone-Long.mp3";
    
    private int volume = 70;
    
    // Constructors
    public AlarmDto() {}
    
    public AlarmDto(LocalTime alarmTime, String label, boolean active, String soundFile, int volume) {
        this.alarmTime = alarmTime;
        this.label = label;
        this.active = active;
        this.soundFile = soundFile;
        this.volume = volume;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalTime getAlarmTime() {
        return alarmTime;
    }
    
    public void setAlarmTime(LocalTime alarmTime) {
        this.alarmTime = alarmTime;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getSoundFile() {
        return soundFile;
    }
    
    public void setSoundFile(String soundFile) {
        this.soundFile = soundFile;
    }
    
    public int getVolume() {
        return volume;
    }
    
    public void setVolume(int volume) {
        this.volume = volume;
    }
    
    // Convert to Entity
    public Alarm toEntity() {
        Alarm alarm = new Alarm();
        alarm.setAlarmTime(this.alarmTime);
        alarm.setLabel(this.label);
        alarm.setActive(this.active);
        alarm.setSoundFile(this.soundFile);
        alarm.setVolume(this.volume);
        return alarm;
    }
    
    // Convert from Entity
    public static AlarmDto fromEntity(Alarm alarm) {
        AlarmDto dto = new AlarmDto();
        dto.setId(alarm.getId());
        dto.setAlarmTime(alarm.getAlarmTime());
        dto.setLabel(alarm.getLabel());
        dto.setActive(alarm.isActive());
        dto.setSoundFile(alarm.getSoundFile());
        dto.setVolume(alarm.getVolume());
        return dto;
    }
}