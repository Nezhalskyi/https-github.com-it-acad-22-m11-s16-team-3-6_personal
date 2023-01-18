package com.softserve.itacademy.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class Utilities {
    private static final String ADMIN = "ADMIN";
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static ToDoUserDetails getUserDetails() {
        return (ToDoUserDetails) getAuthentication().getPrincipal();
    }

    public static boolean isAdmin() {
        return getUserDetails().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ADMIN::equals);
    }

    public static String getUserName() {
        return getUserDetails().getUserName();
    }
}
