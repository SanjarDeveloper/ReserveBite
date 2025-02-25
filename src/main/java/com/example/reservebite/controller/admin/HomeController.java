package com.example.reservebite.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String getHome(){
        return "redirect:/index.html";
    }
}
