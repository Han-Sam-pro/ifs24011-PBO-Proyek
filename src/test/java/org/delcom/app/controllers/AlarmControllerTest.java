package org.delcom.app.controllers;

import org.delcom.app.dto.AlarmDto;
import org.delcom.app.services.AlarmService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmControllerTest {

    @InjectMocks
    private AlarmController alarmController;

    @Mock
    private AlarmService alarmService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    // --- 1. Test Index ---
    @Test
    void testIndex() {
        // Arrange
        when(alarmService.getAllAlarms()).thenReturn(Collections.emptyList());

        // Act
        String viewName = alarmController.index(model);

        // Assert
        assertEquals("index", viewName);
        verify(model).addAttribute(eq("alarmDto"), any(AlarmDto.class));
        verify(model).addAttribute(eq("alarms"), anyList());
        verify(model).addAttribute(eq("currentTime"), anyString());
    }

    // --- 2. Test Save Alarm ---
    @Test
    void testSaveAlarm_Success() {
        // Arrange
        AlarmDto dto = new AlarmDto();
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = alarmController.saveAlarm(dto, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/", viewName);
        verify(alarmService).saveAlarm(dto);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void testSaveAlarm_ValidationErrors() {
        // Arrange
        AlarmDto dto = new AlarmDto();
        when(bindingResult.hasErrors()).thenReturn(true);
        when(alarmService.getAllAlarms()).thenReturn(Collections.emptyList());

        // Act
        String viewName = alarmController.saveAlarm(dto, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("index", viewName); // Harus kembali ke index jika error
        verify(model).addAttribute(eq("alarms"), anyList());
        verify(alarmService, never()).saveAlarm(any()); // Pastikan tidak disimpan
    }

    // --- 3. Test Update Alarm ---
    @Test
    void testUpdateAlarm_Success() {
        // Arrange
        AlarmDto dto = new AlarmDto();
        dto.setId(1L); // ID Valid
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = alarmController.updateAlarm(dto, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/", viewName);
        verify(alarmService).updateAlarm(dto);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void testUpdateAlarm_ValidationErrors() {
        // Arrange
        AlarmDto dto = new AlarmDto();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = alarmController.updateAlarm(dto, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("index", viewName);
        verify(model).addAttribute(eq("alarms"), anyList());
        verify(model).addAttribute(eq("currentTime"), anyString());
        verify(alarmService, never()).updateAlarm(any());
    }

    @Test
    void testUpdateAlarm_IdNull() {
        // Arrange
        AlarmDto dto = new AlarmDto();
        dto.setId(null); // ID Null memicu error
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = alarmController.updateAlarm(dto, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), eq("ID alarm tidak valid!"));
        verify(alarmService, never()).updateAlarm(any());
    }

    @Test
    void testUpdateAlarm_Exception() {
        // Arrange
        AlarmDto dto = new AlarmDto();
        dto.setId(1L);
        when(bindingResult.hasErrors()).thenReturn(false);
        // Simulasi error dari service
        doThrow(new RuntimeException("DB Error")).when(alarmService).updateAlarm(dto);

        // Act
        String viewName = alarmController.updateAlarm(dto, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), contains("Gagal memperbarui alarm"));
    }

    // --- 4. Test Update Label ---
    @Test
    void testUpdateLabel_Success() {
        // Act
        String viewName = alarmController.updateLabel(1L, "NewLabel", redirectAttributes);

        // Assert
        assertEquals("redirect:/", viewName);
        verify(alarmService).updateAlarmLabel(1L, "NewLabel");
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void testUpdateLabel_Exception() {
        // Arrange
        doThrow(new RuntimeException("Error")).when(alarmService).updateAlarmLabel(anyLong(), anyString());

        // Act
        String viewName = alarmController.updateLabel(1L, "Label", redirectAttributes);

        // Assert
        assertEquals("redirect:/", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), contains("Gagal memperbarui label"));
    }

    // --- 5. Test Delete Alarm ---
    @Test
    void testDeleteAlarm() {
        // Act
        String viewName = alarmController.deleteAlarm(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/", viewName);
        verify(alarmService).deleteAlarm(1L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    // --- 6. Test Toggle Alarm ---
    @Test
    void testToggleAlarm_Active() {
        // Arrange
        // Kita mock return value alarmService agar mengembalikan Mock AlarmDto
        AlarmDto mockDto = mock(AlarmDto.class);
        when(mockDto.isActive()).thenReturn(true); // Simulasi status aktif
        when(alarmService.toggleAlarm(1L)).thenReturn(mockDto);

        // Act
        String viewName = alarmController.toggleAlarm(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/", viewName);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Alarm diaktifkan!");
    }

    @Test
    void testToggleAlarm_Inactive() {
        // Arrange
        AlarmDto mockDto = mock(AlarmDto.class);
        when(mockDto.isActive()).thenReturn(false); // Simulasi status non-aktif
        when(alarmService.toggleAlarm(1L)).thenReturn(mockDto);

        // Act
        String viewName = alarmController.toggleAlarm(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/", viewName);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Alarm dinonaktifkan!");
    }

    // --- 7. Test API Endpoints ---
    @Test
    void testGetActiveAlarms() {
        // Arrange
        List<AlarmDto> expectedList = Collections.emptyList();
        when(alarmService.getActiveAlarms()).thenReturn(expectedList);

        // Act
        List<AlarmDto> result = alarmController.getActiveAlarms();

        // Assert
        assertEquals(expectedList, result);
        verify(alarmService).getActiveAlarms();
    }

    @Test
    void testCheckAlarms() {
        // Arrange
        List<AlarmDto> expectedList = Collections.emptyList();
        when(alarmService.getAlarmsTriggeringNow()).thenReturn(expectedList);

        // Act
        List<AlarmDto> result = alarmController.checkAlarms();

        // Assert
        assertEquals(expectedList, result);
        verify(alarmService).getAlarmsTriggeringNow();
    }
}