package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AlarmTest {

    // --- 1. Test Default Constructor & Default Values ---
    @Test
    void testDefaultConstructor() {
        // Act
        Alarm alarm = new Alarm();

        // Assert (Memastikan default value ter-set dengan benar)
        assertNull(alarm.getId());
        assertTrue(alarm.isActive(), "Default active harus true");
        assertEquals("/sound/Yo-Phone-Long.mp3", alarm.getSoundFile(), "Default sound harus sesuai");
        assertEquals(70, alarm.getVolume(), "Default volume harus 70");
        
        // Memastikan field lain null/false
        assertNull(alarm.getImageUrl());
        assertFalse(alarm.getIsLogo());
    }

    // --- 2. Test Parameterized Constructor ---
    @Test
    void testParameterizedConstructor() {
        // Arrange
        LocalTime time = LocalTime.of(8, 30);
        String label = "Bangun Pagi";
        boolean active = false;
        String sound = "bell.mp3";
        int volume = 100;

        // Act
        Alarm alarm = new Alarm(time, label, active, sound, volume);

        // Assert
        assertEquals(time, alarm.getAlarmTime());
        assertEquals(label, alarm.getLabel());
        assertFalse(alarm.isActive());
        assertEquals(sound, alarm.getSoundFile());
        assertEquals(volume, alarm.getVolume());
        
        // Constructor ini tidak meng-set imageUrl/isLogo, pastikan null/false
        assertNull(alarm.getId());
        assertNull(alarm.getImageUrl());
    }

    // --- 3. Test Getters & Setters (Coverage untuk semua field) ---
    @Test
    void testGettersAndSetters() {
        // Arrange
        Alarm alarm = new Alarm();
        LocalTime time = LocalTime.of(12, 0);
        
        // Act (Set semua field)
        alarm.setId(55L);
        alarm.setAlarmTime(time);
        alarm.setLabel("Istirahat Siang");
        alarm.setActive(true);
        alarm.setSoundFile("ring.mp3");
        alarm.setVolume(50);
        alarm.setImageUrl("/uploads/image.png");
        alarm.setIsLogo(true);

        // Assert (Get dan verifikasi)
        assertEquals(55L, alarm.getId());
        assertEquals(time, alarm.getAlarmTime());
        assertEquals("Istirahat Siang", alarm.getLabel());
        assertTrue(alarm.isActive());
        assertEquals("ring.mp3", alarm.getSoundFile());
        assertEquals(50, alarm.getVolume());
        assertEquals("/uploads/image.png", alarm.getImageUrl());
        assertTrue(alarm.getIsLogo());
    }
    
    // --- 4. Test toString Method ---
    @Test
    void testToString() {
        // Arrange
        Alarm alarm = new Alarm();
        alarm.setId(1L);
        alarm.setLabel("Test Label");
        alarm.setVolume(88);
        
        // Act
        String result = alarm.toString();

        // Assert
        assertNotNull(result);
        // Memastikan string mengandung nilai yang kita set
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("label='Test Label'"));
        assertTrue(result.contains("volume=88"));
        
        // Memastikan format umum toString (Alarm{...})
        assertTrue(result.startsWith("Alarm{"));
        assertTrue(result.endsWith("}"));
    }
}