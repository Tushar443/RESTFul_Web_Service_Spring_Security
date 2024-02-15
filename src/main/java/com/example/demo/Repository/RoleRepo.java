package com.example.demo.Repository;

import com.example.demo.ws.io.Entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByName(String name);
}
