package org.delcom.app.services;

import org.delcom.app.dto.AlarmDto;
import org.delcom.app.entities.Alarm;
import org.delcom.app.repositories.AlarmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    private AlarmRepository alarmRepository;

    @InjectMocks
    private AlarmService alarmService;

    // Dummy objects
    private Alarm alarm;
    private AlarmDto alarmDto;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        // Setup Entity
        alarm = new Alarm();
        alarm.setId(1L);
        alarm.setAlarmTime(LocalTime.of(8, 0));
        alarm.setActive(true);
        alarm.setLabel("Pagi");

        // Setup DTO
        alarmDto = new AlarmDto();
        alarmDto.setId(1L);
        alarmDto.setAlarmTime(LocalTime.of(8, 0));
        alarmDto.setActive(true);
        alarmDto.setLabel("Pagi");

        // Setup Mock File yang Valid
        mockFile = mock(MultipartFile.class);
    }

    // ---------------------------------------------------------------
    // 1. COVERAGE UNTUK GET METHODS (Termasuk getActiveAlarms yang merah)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Test Get All Alarms")
    void testGetAllAlarms() {
        when(alarmRepository.findAllByOrderByAlarmTimeAsc()).thenReturn(Collections.singletonList(alarm));
        List<AlarmDto> result = alarmService.getAllAlarms();
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Test Get Active Alarms (Fixes Red Line)")
    void testGetActiveAlarms() {
        // Ini menutupi baris merah pada method getActiveAlarms()
        when(alarmRepository.findByActiveTrueOrderByAlarmTimeAsc()).thenReturn(Collections.singletonList(alarm));
        List<AlarmDto> result = alarmService.getActiveAlarms();
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Test Get Alarm By Id - Success (Fixes Red Line)")
    void testGetAlarmById_Success() {
        // Ini menutupi baris return sukses pada getAlarmById
        when(alarmRepository.findById(1L)).thenReturn(Optional.of(alarm));
        AlarmDto result = alarmService.getAlarmById(1L);
        assertNotNull(result);
        assertEquals(alarm.getId(), result.getId());
    }

    @Test
    @DisplayName("Test Get Alarm By Id - Not Found")
    void testGetAlarmById_NotFound() {
        when(alarmRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> alarmService.getAlarmById(99L));
    }

    @Test
    @DisplayName("Test Get Alarms Triggering Now")
    void testGetAlarmsTriggeringNow() {
        when(alarmRepository.findActiveAlarmsByTime(any(LocalTime.class)))
                .thenReturn(Collections.singletonList(alarm));
        List<AlarmDto> result = alarmService.getAlarmsTriggeringNow();
        assertNotNull(result);
    }

    // ---------------------------------------------------------------
    // 2. COVERAGE UNTUK SAVE & UPDATE (Termasuk IOException catch block)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Test Save Alarm - Success With File")
    void testSaveAlarm_WithFile() throws IOException {
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));
        alarmDto.setImageFile(mockFile);

        when(alarmRepository.save(any(Alarm.class))).thenReturn(alarm);

        // Bungkus assertDoesNotThrow untuk menangani potensi error permission folder asli
        assertDoesNotThrow(() -> alarmService.saveAlarm(alarmDto));
    }

    @Test
    @DisplayName("Test Save Alarm - Force IOException (Fixes Red Screenshot 2)")
    void testSaveAlarm_ForcedIOException() throws IOException {
        // TRIK: Kita paksa getInputStream melempar IOException.
        // Ini akan memicu catch(IOException e) di dalam method private saveFileToLocal.
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("error.jpg");
        when(mockFile.getInputStream()).thenThrow(new IOException("Disk Full")); // Force Error

        alarmDto.setImageFile(mockFile);

        // Harapan: Method melempar RuntimeException dengan pesan "Gagal menyimpan..."
        RuntimeException ex = assertThrows(RuntimeException.class, () -> alarmService.saveAlarm(alarmDto));
        
        // Verifikasi kita masuk ke blok catch yang merah itu
        assertTrue(ex.getMessage().contains("Gagal menyimpan file gambar"));
    }

    @Test
    @DisplayName("Test Update Alarm - With File (Fixes Red in updateAlarm)")
    void testUpdateAlarm_WithFile() throws IOException {
        // Skenario update membawa file baru -> Masuk ke if (file != null)
        when(alarmRepository.findById(1L)).thenReturn(Optional.of(alarm));
        
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("update.jpg");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("new data".getBytes()));
        alarmDto.setImageFile(mockFile);

        when(alarmRepository.save(any(Alarm.class))).thenReturn(alarm);

        assertDoesNotThrow(() -> alarmService.updateAlarm(alarmDto));
    }

    @Test
    @DisplayName("Test Update Alarm - Not Found")
    void testUpdateAlarm_NotFound() {
        when(alarmRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> alarmService.updateAlarm(alarmDto));
    }

    // ---------------------------------------------------------------
    // 3. COVERAGE UNTUK TOGGLE & LABEL (Fixes orElseThrow Red Lines)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Test Toggle Alarm - Success")
    void testToggleAlarm_Success() {
        when(alarmRepository.findById(1L)).thenReturn(Optional.of(alarm));
        when(alarmRepository.save(any(Alarm.class))).thenReturn(alarm);

        AlarmDto result = alarmService.toggleAlarm(1L);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test Toggle Alarm - Not Found (Fixes Red Line)")
    void testToggleAlarm_NotFound() {
        // Ini menutupi baris .orElseThrow(...) pada toggleAlarm
        when(alarmRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> alarmService.toggleAlarm(99L));
        assertTrue(ex.getMessage().contains("Alarm not found"));
    }

    @Test
    @DisplayName("Test Update Alarm Label - Success")
    void testUpdateAlarmLabel_Success() {
        when(alarmRepository.findById(1L)).thenReturn(Optional.of(alarm));
        when(alarmRepository.save(any(Alarm.class))).thenReturn(alarm);

        assertDoesNotThrow(() -> alarmService.updateAlarmLabel(1L, "New Label"));
        assertEquals("New Label", alarm.getLabel());
    }

    @Test
    @DisplayName("Test Update Alarm Label - Not Found (Fixes Red Line)")
    void testUpdateAlarmLabel_NotFound() {
        // Ini menutupi baris .orElseThrow(...) pada updateAlarmLabel
        when(alarmRepository.findById(99L)).thenReturn(Optional.empty());
        
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            alarmService.updateAlarmLabel(99L, "New Label");
        });
        assertTrue(ex.getMessage().contains("Alarm not found"));
    }

    @Test
    @DisplayName("Test Delete Alarm")
    void testDeleteAlarm() {
        doNothing().when(alarmRepository).deleteById(1L);
        alarmService.deleteAlarm(1L);
        verify(alarmRepository, times(1)).deleteById(1L);
    }
}