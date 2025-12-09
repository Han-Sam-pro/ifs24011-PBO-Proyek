package org.delcom.app.services;

import org.delcom.app.dto.AlarmDto;
import org.delcom.app.entities.Alarm;
import org.delcom.app.repositories.AlarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; // Import wajib

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlarmService {
    
    @Autowired
    private AlarmRepository alarmRepository;
    
    // Folder tujuan penyimpanan: src/main/resources/static/uploads
    // Agar bisa diakses browser dengan url: localhost:8080/uploads/namafile.jpg
    private final Path rootLocation = Paths.get("src/main/resources/static/uploads");

    // Constructor untuk memastikan folder upload ada saat aplikasi start
    public AlarmService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }
    
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
    
    // --- METHOD SAVE (CREATE) ---
    public AlarmDto saveAlarm(AlarmDto alarmDto) {
        // 1. Cek apakah user upload file gambar
        if (alarmDto.getImageFile() != null && !alarmDto.getImageFile().isEmpty()) {
            // 2. Simpan file fisik
            String fileName = saveFileToLocal(alarmDto.getImageFile());
            // 3. Set path URL untuk disimpan di database
            alarmDto.setImageUrl("/uploads/" + fileName);
        }

        // 4. Konversi DTO (yang sudah punya imageUrl) ke Entity
        Alarm alarm = alarmDto.toEntity();
        Alarm savedAlarm = alarmRepository.save(alarm);
        return AlarmDto.fromEntity(savedAlarm);
    }
    
    // --- METHOD UPDATE ---
    public void updateAlarm(AlarmDto alarmDto) {
        Alarm alarm = alarmRepository.findById(alarmDto.getId())
            .orElseThrow(() -> new RuntimeException("Alarm tidak ditemukan dengan id: " + alarmDto.getId()));
        
        // Update data dasar
        alarm.setAlarmTime(alarmDto.getAlarmTime());
        alarm.setLabel(alarmDto.getLabel());
        alarm.setActive(alarmDto.isActive());
        
        // Update setting Logo
        alarm.setIsLogo(alarmDto.getIsLogo());

        // Update Gambar (Jika ada file baru)
        if (alarmDto.getImageFile() != null && !alarmDto.getImageFile().isEmpty()) {
            String fileName = saveFileToLocal(alarmDto.getImageFile());
            alarm.setImageUrl("/uploads/" + fileName);
        }
        // Jika tidak upload file baru, imageUrl lama di Entity tidak diubah (tetap aman)
        
        alarmRepository.save(alarm);
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
    
    public void updateAlarmLabel(Long id, String newLabel) {
        Alarm alarm = alarmRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alarm not found"));
        alarm.setLabel(newLabel);
        alarmRepository.save(alarm);
    }

    // --- HELPER: LOGIKA SIMPAN FILE FISIK ---
    private String saveFileToLocal(MultipartFile file) {
        try {
            // Bersihkan nama file
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) originalFilename = "unknown.jpg";
            
            // Ambil ekstensi (.jpg, .png)
            String ext = "";
            int i = originalFilename.lastIndexOf('.');
            if (i > 0) {
                ext = originalFilename.substring(i);
            }
            
            // Buat nama file unik dengan timestamp agar tidak bentrok
            String newFileName = System.currentTimeMillis() + ext;
            
            // Proses copy file ke folder
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, rootLocation.resolve(newFileName), 
                           StandardCopyOption.REPLACE_EXISTING);
            }
            
            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file gambar", e);
        }
    }
}