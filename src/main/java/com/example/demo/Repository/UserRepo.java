package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.ws.io.Entity.UserEntity;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {

	UserEntity findUserByEmail(String email);

	UserEntity findByUserId(String id);

}
