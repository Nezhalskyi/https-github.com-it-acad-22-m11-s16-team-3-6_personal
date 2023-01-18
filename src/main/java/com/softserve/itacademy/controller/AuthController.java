package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AuthController {

    @GetMapping("/form-login")
    public String getLoginPage(){
        return "login";
    }

    @GetMapping("/form-signup")
    public String getRegistrationPage(@ModelAttribute("user") User user){
        return "signup";
    }
}
