package com.softserve.itacademy.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class ToDoUserDetails extends User {
    private final Long id;
    private final String userName;
    public ToDoUserDetails(String username, String password,
                              Collection<? extends GrantedAuthority> authorities, Long id, String userName) {
        super(username, password, authorities);
        this.id = id;
        this.userName = userName;
    }
}
