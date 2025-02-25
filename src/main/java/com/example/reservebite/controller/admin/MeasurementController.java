package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Measurement;
import com.example.reservebite.service.MeasurementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;

    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @GetMapping
    public String listMeasurements(Model model) {
        model.addAttribute("measurements", measurementService.getAllMeasurements());
        return "admin/measurements/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("measurement", new Measurement());
        model.addAttribute("isActive", true);
        return "admin/measurements/create";
    }

    @PostMapping
    public String createMeasurement(@ModelAttribute("measurement") Measurement measurement) {
        measurementService.saveMeasurement(measurement);
        return "redirect:/admin/measurements";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Measurement measurement = measurementService.getMeasurementById(id);
        model.addAttribute("measurement", measurement);
        return "admin/measurements/edit";
    }

    @PostMapping("/update/{id}")
    public String updateMeasurement(@PathVariable Long id, @ModelAttribute("measurement") Measurement measurement) {
        measurementService.editMeasurement(id,measurement);
        return "redirect:/admin/measurements";
    }


    @GetMapping("/delete/{id}")
    public String deleteMeasurement(@PathVariable Long id) {
        measurementService.deleteMeasurement(id);
        return "redirect:/admin/measurements";
    }
}
