package com.example.reservebite.component;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class WebConfig {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Custom editor for LocalTime
        binder.registerCustomEditor(LocalTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text != null && !text.isEmpty()) {
                    LocalTime localTime = LocalTime.parse(text, DateTimeFormatter.ofPattern("HH:mm"));
                    setValue(localTime);
                } else {
                    setValue(null);
                }
            }

            @Override
            public String getAsText() {
                LocalTime localTime = (LocalTime) getValue();
                if (localTime != null) {
                    return localTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                }
                return "";
            }
        });
    }
}