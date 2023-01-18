package com.softserve.itacademy.controller;

import com.softserve.itacademy.repository.UserRepository;
import com.softserve.itacademy.security.Utilities;
import com.softserve.itacademy.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {
    private final UserService userService;
    private final UserRepository userRepository;

    public HomeController(UserService userService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping({"/", "home"})
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public String home(Model model) {
        model.addAttribute("users", userService.getAll());
        model.addAttribute("userName", Utilities.getUserName());

        System.out.println(Utilities.getUserDetails().getId());
        System.out.println(Utilities.getUserName());

        return "home";
    }
}
