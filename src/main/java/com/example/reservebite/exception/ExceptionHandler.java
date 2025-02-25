package com.example.reservebite.exception;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExceptionHandler {

    @GetMapping("/incorrect-cuisine-deletion-exception")
    public String exception(Model model) {
        model.addAttribute("errorMessage", "Please delete restaurants first");
        return "exceptions/exception";
    }

}
