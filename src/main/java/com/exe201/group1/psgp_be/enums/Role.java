package com.exe201.group1.psgp_be.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public enum Role {
    BUYER,
    ADMIN,
    SELLER;

    public List<SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.name().toUpperCase()));
    }
}
