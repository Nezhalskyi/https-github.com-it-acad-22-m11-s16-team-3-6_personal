package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.UserRepository;
import com.softserve.itacademy.security.Utilities;
import com.softserve.itacademy.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


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
        List<User> users = userService.getAll();
        if (!Utilities.isAdmin()) {
            users.removeIf(u -> u.getId() != Utilities.getUserDetails().getId());
        }
        model.addAttribute("users", users);
        model.addAttribute("userName", Utilities.getUserName());
        model.addAttribute("userId", Utilities.getUserDetails().getId());
        return "home";
    }
}
