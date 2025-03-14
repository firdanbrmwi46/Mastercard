package com.mastercard.details;

import com.mastercard.model.users.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUserDetails extends User {
    private String realName;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String realName) {
        super(username, password, authorities);
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }
}
