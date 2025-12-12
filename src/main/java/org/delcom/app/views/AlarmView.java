package org.delcom.app.views;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/alarm")
public class AlarmView {

    public AlarmView() {}

    @GetMapping("")
    public String showAlarmList(Model model) {
        model.addAttribute("alarms", null);
        return "pages/alarm/list";
    }

    @GetMapping("/create")
    public String showCreateAlarm(Model model) {
        model.addAttribute("alarmForm", new Object());
        return "pages/alarm/create";
    }

    @PostMapping("/create/post")
    public String postCreateAlarm(@ModelAttribute("alarmForm") Object alarmForm) {
        return "redirect:/alarm";
    }

    @GetMapping("/edit/{id}")
    public String showEditAlarm(@PathVariable String id, Model model) {
        model.addAttribute("alarm", null);
        return "pages/alarm/edit";
    }

    @PostMapping("/edit/{id}/post")
    public String postEditAlarm(@PathVariable String id, @ModelAttribute("alarmForm") Object alarmForm) {
        return "redirect:/alarm";
    }

    @GetMapping("/delete/{id}")
    public String deleteAlarm(@PathVariable String id) {
        return "redirect:/alarm";
    }
}
