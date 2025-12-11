package org.delcom.app.dto;

import org.delcom.app.entities.Alarm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmDtoTest {

    @Mock
    private MultipartFile mockFile;

    // --- 1. Test Constructor & Default Values ---
    @Test
    void testConstructorAndDefaultValues() {
        AlarmDto dto = new AlarmDto();

        // Cek default value yang didefinisikan di field
        assertTrue(dto.isActive(), "Default active harus true");
        assertEquals("/sound/Yo-Phone-Long.mp3", dto.getSoundFile(), "Default sound file harus sesuai");
        assertEquals(70, dto.getVolume(), "Default volume harus 70");
        assertNull(dto.getId());
    }

    // --- 2. Test Getters & Setters (Coverage untuk fields) ---
    @Test
    void testGettersAndSetters() {
        AlarmDto dto = new AlarmDto();
        LocalTime time = LocalTime.of(8, 0);

        // Set Values
        dto.setId(100L);
        dto.setAlarmTime(time);
        dto.setLabel("Morning Alarm");
        dto.setActive(false);
        dto.setImageUrl("/images/test.png");
        dto.setImageFile(mockFile); // Menggunakan Mock MultipartFile
        dto.setIsLogo(true);
        dto.setSoundFile("alert.mp3");
        dto.setVolume(50);

        // Get & Assert Values
        assertEquals(100L, dto.getId());
        assertEquals(time, dto.getAlarmTime());
        assertEquals("Morning Alarm", dto.getLabel());
        assertFalse(dto.isActive());
        assertEquals("/images/test.png", dto.getImageUrl());
        assertEquals(mockFile, dto.getImageFile());
        assertTrue(dto.getIsLogo()); // Perhatikan method getIsLogo()
        assertEquals("alert.mp3", dto.getSoundFile());
        assertEquals(50, dto.getVolume());
    }

    // --- 3. Test Method toEntity (Logic Mapping) ---
    
    @Test
    void testToEntity_FullData() {
        // Arrange
        AlarmDto dto = new AlarmDto();
        LocalTime time = LocalTime.of(7, 30);
        
        dto.setId(1L);
        dto.setAlarmTime(time);
        dto.setLabel("Work");
        dto.setActive(true);
        dto.setSoundFile("bell.mp3");
        dto.setVolume(80);
        dto.setImageUrl("img.jpg");
        dto.setIsLogo(true);
        
        // Act
        // Asumsi: Class Alarm (Entity) adalah POJO standard
        Alarm entity = dto.toEntity();

        // Assert
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals(time, entity.getAlarmTime());
        assertEquals("Work", entity.getLabel());
        assertTrue(entity.isActive());
        assertEquals("bell.mp3", entity.getSoundFile());
        assertEquals(80, entity.getVolume());
        assertEquals("img.jpg", entity.getImageUrl());
        assertTrue(entity.getIsLogo());
    }

    @Test
    void testToEntity_NullId() {
        // Test case ini PENTING untuk coverage cabang "if (this.id != null)"
        
        // Arrange
        AlarmDto dto = new AlarmDto();
        dto.setId(null); // ID Null (kasus create baru)
        dto.setAlarmTime(LocalTime.now());
        dto.setLabel("New");

        // Act
        Alarm entity = dto.toEntity();

        // Assert
        // Pastikan tidak error dan ID di entity null (atau default 0/null tergantung entity)
        // Di method toEntity Anda: if (this.id != null) { alarm.setId(...) }
        // Jadi setId tidak dipanggil.
        assertNull(entity.getId(), "Entity ID harus null jika DTO ID null");
        assertEquals("New", entity.getLabel());
    }

    // --- 4. Test Method fromEntity (Static Method) ---
    
    @Test
    void testFromEntity() {
        // Arrange
        // Kita mock Entity Alarm untuk memastikan isolasi, 
        // atau gunakan real object jika Alarm hanya POJO sederhana.
        // Disini menggunakan Mockito spy/mock agar fleksibel.
        Alarm mockAlarm = mock(Alarm.class);
        LocalTime time = LocalTime.of(12, 0);

        when(mockAlarm.getId()).thenReturn(55L);
        when(mockAlarm.getAlarmTime()).thenReturn(time);
        when(mockAlarm.getLabel()).thenReturn("Lunch");
        when(mockAlarm.isActive()).thenReturn(true);
        when(mockAlarm.getSoundFile()).thenReturn("ring.mp3");
        when(mockAlarm.getVolume()).thenReturn(100);
        when(mockAlarm.getImageUrl()).thenReturn("lunch.png");
        when(mockAlarm.getIsLogo()).thenReturn(false);

        // Act
        AlarmDto dto = AlarmDto.fromEntity(mockAlarm);

        // Assert
        assertNotNull(dto);
        assertEquals(55L, dto.getId());
        assertEquals(time, dto.getAlarmTime());
        assertEquals("Lunch", dto.getLabel());
        assertTrue(dto.isActive());
        assertEquals("ring.mp3", dto.getSoundFile());
        assertEquals(100, dto.getVolume());
        assertEquals("lunch.png", dto.getImageUrl());
        assertFalse(dto.getIsLogo());
        
        // Verifikasi bahwa imageFile null (karena tidak ada di entity)
        assertNull(dto.getImageFile());
    }
}