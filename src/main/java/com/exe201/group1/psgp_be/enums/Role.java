package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Role {
    BUYER("buyer"),
    ADMIN("admin"),
    SELLER("seller");

    private final String value;

    public List<SimpleGrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(
                "ROLE_" + this.name().toUpperCase()));
    }
}
