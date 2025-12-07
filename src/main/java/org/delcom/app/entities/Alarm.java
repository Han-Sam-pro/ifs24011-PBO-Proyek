package org.delcom.app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@Entity
@Table(name = "alarms")
public class Alarm {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Waktu alarm tidak boleh kosong")
    @Column(name = "alarm_time", nullable = false)
    private LocalTime alarmTime;
    
    @NotBlank(message = "Label alarm tidak boleh kosong")
    @Column(name = "label", nullable = false)
    private String label;
    
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    
    @Column(name = "sound_file")
    private String soundFile = "/sound/Yo-Phone-Long.mp3";
    
    @Column(name = "volume")
    private int volume = 70;
    
    // Constructors
    public Alarm() {}
    
    public Alarm(LocalTime alarmTime, String label, boolean active, String soundFile, int volume) {
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
    
    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", alarmTime=" + alarmTime +
                ", label='" + label + '\'' +
                ", active=" + active +
                ", soundFile='" + soundFile + '\'' +
                ", volume=" + volume +
                '}';
    }
}