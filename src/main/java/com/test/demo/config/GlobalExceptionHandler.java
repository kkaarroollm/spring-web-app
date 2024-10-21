package com.test.demo.config;

import com.test.demo.exceptions.EmailNotFoundException;
import com.test.demo.exceptions.EmailNotVerifiedException;
import com.test.demo.exceptions.UserDisabledException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UserDisabledException.class, EmailNotVerifiedException.class, EmailNotFoundException.class})
    public String handleAuthenticationExceptions(RuntimeException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "redirect:/login?error=" + ex.getMessage();
    }
}
