package com.example.demo.Repository;

import com.example.demo.ws.io.Entity.AddressEntity;
import com.example.demo.ws.io.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepo extends JpaRepository<AddressEntity,Long> {

    List<AddressEntity> findAllByUserDetails(UserEntity userEntity);

    AddressEntity findByAddressId(String addressId);
}
