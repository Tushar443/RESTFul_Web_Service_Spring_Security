package com.example.demo;

import com.example.demo.Repository.AuthorityRepo;
import com.example.demo.Repository.RoleRepo;
import com.example.demo.Repository.UserRepo;
import com.example.demo.ws.io.Entity.AuthorityEntity;
import com.example.demo.ws.io.Entity.RoleEntity;
import com.example.demo.ws.io.Entity.UserEntity;
import com.example.demo.ws.shared.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;

@Component
public class InitialUsersSetup {

    @Autowired
    AuthorityRepo authorityRepo;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    MyUtils utils;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");

        RoleEntity roleUser = createRole("ROLE_USER", Arrays.asList(readAuthority, writeAuthority));
        RoleEntity roleAdmin = createRole("ROLE_ADMIN", Arrays.asList(readAuthority, writeAuthority, deleteAuthority));

        if (roleAdmin != null) return;

        UserEntity adminUser = new UserEntity();
        adminUser.setFirstName("Tushar");
        adminUser.setLastName("More");
        adminUser.setEmail("moretushar@gmail.com");
        adminUser.setEmailVerificationStatus(true);
        adminUser.setUserId(utils.generateUserId(30));
        adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("1234"));
        adminUser.setRoles(Arrays.asList(roleAdmin));
        userRepo.save(adminUser);
    }

    @Transactional
    private AuthorityEntity createAuthority(String name) {
        AuthorityEntity authority = authorityRepo.findByName(name);
        if (authority == null) {
            authority = new AuthorityEntity(name);
            authorityRepo.save(authority);
        }
        return authority;
    }

    @Transactional
    private RoleEntity createRole(String name, Collection<AuthorityEntity> authorityEntities) {
        RoleEntity roleEntity = roleRepo.findByName(name);
        if (roleEntity == null) {
            roleEntity = new RoleEntity(name);
            roleEntity.setAuthorities(authorityEntities);
            roleRepo.save(roleEntity);
        }
        return roleEntity;
    }
}
