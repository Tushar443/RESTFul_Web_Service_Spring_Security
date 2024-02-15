package com.example.demo.Security;

import com.example.demo.ws.io.Entity.AuthorityEntity;
import com.example.demo.ws.io.Entity.RoleEntity;
import com.example.demo.ws.io.Entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserPrinciple implements UserDetails {
    @Serial
    private static final long serialVersionUID = 4256409871647316235L;
    @Autowired
    private UserEntity userEntity;

    public UserPrinciple(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> returnGrantAuthority = new ArrayList<>();
        List<AuthorityEntity> authorityEntities = new ArrayList<>();
        Collection<RoleEntity> roles = userEntity.getRoles();
        if (roles == null) return returnGrantAuthority;
        roles.forEach((role -> {
            returnGrantAuthority.add(new SimpleGrantedAuthority(role.getName()));
            authorityEntities.addAll(role.getAuthorities());
        }));

        authorityEntities.forEach((authority -> {
            returnGrantAuthority.add(new SimpleGrantedAuthority(authority.getName()));
        }));

        return returnGrantAuthority;
    }

    @Override
    public String getPassword() {
        return userEntity.getEncryptedPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userEntity.getEmailVerificationStatus();
    }
}
