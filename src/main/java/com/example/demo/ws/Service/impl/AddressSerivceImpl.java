package com.example.demo.ws.Service.impl;

import com.example.demo.Repository.AddressRepo;
import com.example.demo.Repository.UserRepo;
import com.example.demo.ws.Service.AddressServiceIfc;
import com.example.demo.ws.io.Entity.AddressEntity;
import com.example.demo.ws.io.Entity.UserEntity;
import com.example.demo.ws.shared.dto.AddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressSerivceImpl implements AddressServiceIfc {

    @Autowired
    UserRepo userRepo;

    @Autowired
    AddressRepo addressRepo;

    /**
     * @param userId
     * @return
     */
    @Override
    public List<AddressDTO> getAddresses(String userId) {
        List<AddressDTO> returnValue = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = userRepo.findByUserId(userId);
        if(userEntity == null){ return returnValue;}
        Iterable<AddressEntity> addressEntities = addressRepo.findAllByUserDetails(userEntity);
        for(AddressEntity address : addressEntities){
            returnValue.add(modelMapper.map(address, AddressDTO.class));
        }
        return returnValue;
    }

    /**
     * @param addressId
     * @return
     */
    @Override
    public AddressDTO getAddress(String addressId) {
        AddressDTO addressDTO = new AddressDTO();
        ModelMapper modelMapper = new ModelMapper();
        AddressEntity addressEntity = addressRepo.findByAddressId(addressId);
        addressDTO = modelMapper.map(addressEntity, AddressDTO.class);
        return addressDTO;
    }
}
