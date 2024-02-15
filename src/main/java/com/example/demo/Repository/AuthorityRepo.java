package com.example.demo.Repository;

import com.example.demo.ws.io.Entity.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepo extends JpaRepository<AuthorityEntity, Long> {

    AuthorityEntity findByName(String name);
}
