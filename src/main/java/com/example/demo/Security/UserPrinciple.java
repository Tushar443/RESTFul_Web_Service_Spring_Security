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
import java.util.HashSet;
import java.util.List;

public class UserPrinciple implements UserDetails {
    @Serial
    private static final long serialVersionUID = 4256409871647316235L;
    @Autowired
    private UserEntity userEntity;

    private String userId;
    public UserPrinciple(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.userId = userEntity.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> returnGrantAuthority = new HashSet<>();
        Collection<AuthorityEntity> authorityEntities = new HashSet<>();
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

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
