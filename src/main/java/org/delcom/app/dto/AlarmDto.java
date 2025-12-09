package org.delcom.app.dto;

import org.delcom.app.entities.Alarm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile; // PENTING: Import ini

import java.time.LocalTime;

public class AlarmDto {
    
    private Long id;
    
    @NotNull(message = "Waktu alarm tidak boleh kosong")
    private LocalTime alarmTime;
    
    @NotBlank(message = "Label alarm tidak boleh kosong")
    private String label;
    
    private boolean active = true;

    // --- FIELD BARU UNTUK GAMBAR ---
    // 1. String path untuk disimpan/diambil dari database (Entity)
    private String imageUrl; 

    // 2. File fisik dari form HTML (Tidak ada di Entity, hanya di DTO)
    private MultipartFile imageFile; 

    // 3. Status tampilan logo
    private boolean isLogo; 
    // -------------------------------
    
    private String soundFile = "/sound/Yo-Phone-Long.mp3";
    private int volume = 70;
    
    // Constructors
    public AlarmDto() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalTime getAlarmTime() { return alarmTime; }
    public void setAlarmTime(LocalTime alarmTime) { this.alarmTime = alarmTime; }
    
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    // Getter Setter ImageUrl (String)
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Getter Setter ImageFile (MultipartFile) - PENTING
    public MultipartFile getImageFile() { return imageFile; }
    public void setImageFile(MultipartFile imageFile) { this.imageFile = imageFile; }

    // Getter Setter IsLogo
    public boolean getIsLogo() { return isLogo; }
    public void setIsLogo(boolean logo) { this.isLogo = logo; }

    public String getSoundFile() { return soundFile; }
    public void setSoundFile(String soundFile) { this.soundFile = soundFile; }
    
    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = volume; }
    
    // --- PENYESUAIAN MAPPING KE ENTITY ANDA ---
    public Alarm toEntity() {
        Alarm alarm = new Alarm();
        if (this.id != null) {
            alarm.setId(this.id);
        }
        alarm.setAlarmTime(this.alarmTime);
        alarm.setLabel(this.label);
        alarm.setActive(this.active);
        alarm.setSoundFile(this.soundFile);
        alarm.setVolume(this.volume);
        
        // Mapping kolom baru
        alarm.setImageUrl(this.imageUrl);
        alarm.setIsLogo(this.isLogo);
        
        return alarm;
    }
    
    public static AlarmDto fromEntity(Alarm alarm) {
        AlarmDto dto = new AlarmDto();
        dto.setId(alarm.getId());
        dto.setAlarmTime(alarm.getAlarmTime());
        dto.setLabel(alarm.getLabel());
        dto.setActive(alarm.isActive());
        dto.setSoundFile(alarm.getSoundFile());
        dto.setVolume(alarm.getVolume());
        
        // Mapping kolom baru
        dto.setImageUrl(alarm.getImageUrl());
        // Menggunakan getter yang Anda buat di Entity: getIsLogo()
        dto.setIsLogo(alarm.getIsLogo()); 
        
        return dto;
    }
}