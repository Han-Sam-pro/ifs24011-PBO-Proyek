package org.delcom.app.controllers;

import org.delcom.app.dto.AlarmDto;
import org.delcom.app.services.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalTime;

@Controller
@RequestMapping("/")
public class AlarmController {
    
    @Autowired
    private AlarmService alarmService;
    
    
    @GetMapping
    public String index(Model model) {
        model.addAttribute("alarmDto", new AlarmDto());
        model.addAttribute("alarms", alarmService.getAllAlarms());
        model.addAttribute("currentTime", LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        return "index";
    }
    
    @PostMapping("/save")
    public String saveAlarm(@Valid @ModelAttribute AlarmDto alarmDto, 
                           BindingResult result, 
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("alarms", alarmService.getAllAlarms());
            return "index";
        }
        
        alarmService.saveAlarm(alarmDto);
        redirectAttributes.addFlashAttribute("successMessage", "Alarm berhasil disimpan!");
        return "redirect:/";
    }
    
    // TAMBAHKAN: Endpoint untuk update alarm (waktu dan label)
    @PostMapping("/update")
    public String updateAlarm(@Valid @ModelAttribute AlarmDto alarmDto, 
                             BindingResult result, 
                             Model model,
                             RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("alarms", alarmService.getAllAlarms());
            model.addAttribute("currentTime", LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
            return "index";
        }
        
        try {
            // Validasi ID
            if (alarmDto.getId() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "ID alarm tidak valid!");
                return "redirect:/";
            }
            
            // Update alarm menggunakan service
            alarmService.updateAlarm(alarmDto);
            redirectAttributes.addFlashAttribute("successMessage", "Alarm berhasil diperbarui!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal memperbarui alarm: " + e.getMessage());
        }
        
        return "redirect:/";
    }
    
    // TAMBAHKAN: Endpoint untuk update label saja
    @PostMapping("/update-label")
    public String updateLabel(@RequestParam("id") Long id,
                             @RequestParam("label") String label,
                             RedirectAttributes redirectAttributes) {
        
        try {
            alarmService.updateAlarmLabel(id, label);
            redirectAttributes.addFlashAttribute("successMessage", "Label alarm berhasil diperbarui!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal memperbarui label: " + e.getMessage());
        }
        
        return "redirect:/";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteAlarm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        alarmService.deleteAlarm(id);
        redirectAttributes.addFlashAttribute("successMessage", "Alarm berhasil dihapus!");
        return "redirect:/";
    }
    
    @PostMapping("/toggle/{id}")
    public String toggleAlarm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        AlarmDto alarm = alarmService.toggleAlarm(id);
        String message = alarm.isActive() ? "Alarm diaktifkan!" : "Alarm dinonaktifkan!";
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/";
    }
    
    @GetMapping("/api/alarms/active")
    @ResponseBody
    public java.util.List<AlarmDto> getActiveAlarms() {
        return alarmService.getActiveAlarms();
    }
    
    @GetMapping("/api/alarms/check")
    @ResponseBody
    public java.util.List<AlarmDto> checkAlarms() {
        return alarmService.getAlarmsTriggeringNow();
    }
}