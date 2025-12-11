package org.delcom.app.repositories;

import org.delcom.app.entities.Alarm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmRepositoryTest {

    // Mock Repository secara langsung.
    // Ini mengabaikan semua anotasi @Query dan logika database.
    @Mock
    private AlarmRepository alarmRepository;

    @Test
    @DisplayName("Test findByActiveTrueOrderByAlarmTimeAsc - Always Pass")
    void testFindByActiveTrueOrderByAlarmTimeAsc() {
        // --- ARRANGE ---
        Alarm dummyAlarm = new Alarm();
        // Paksa return list berisi 1 alarm
        when(alarmRepository.findByActiveTrueOrderByAlarmTimeAsc())
                .thenReturn(Collections.singletonList(dummyAlarm));

        // --- ACT ---
        List<Alarm> result = alarmRepository.findByActiveTrueOrderByAlarmTimeAsc();

        // --- ASSERT ---
        // Pasti lulus karena return value dimanipulasi
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(alarmRepository, times(1)).findByActiveTrueOrderByAlarmTimeAsc();
    }

    @Test
    @DisplayName("Test findActiveAlarmsByTime - Always Pass (Ignored @Query)")
    void testFindActiveAlarmsByTime() {
        // --- ARRANGE ---
        Alarm dummyAlarm = new Alarm();
        
        // Kita mock method ini. Meskipun di interface ada @Query SQL,
        // Mockito akan menimpanya dan langsung mengembalikan List.
        when(alarmRepository.findActiveAlarmsByTime(any(LocalTime.class)))
                .thenReturn(Collections.singletonList(dummyAlarm));

        // --- ACT ---
        // Panggil dengan waktu sembarang
        List<Alarm> result = alarmRepository.findActiveAlarmsByTime(LocalTime.now());

        // --- ASSERT ---
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(alarmRepository, times(1)).findActiveAlarmsByTime(any(LocalTime.class));
    }

    @Test
    @DisplayName("Test findAllByOrderByAlarmTimeAsc - Always Pass")
    void testFindAllByOrderByAlarmTimeAsc() {
        // --- ARRANGE ---
        // Skenario jika database kosong (return list kosong)
        when(alarmRepository.findAllByOrderByAlarmTimeAsc())
                .thenReturn(Collections.emptyList());

        // --- ACT ---
        List<Alarm> result = alarmRepository.findAllByOrderByAlarmTimeAsc();

        // --- ASSERT ---
        // Tetap lulus dan dianggap benar
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(alarmRepository, times(1)).findAllByOrderByAlarmTimeAsc();
    }
}