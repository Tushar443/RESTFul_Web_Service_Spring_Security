package com.example.demo.ws.Service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.UserRepo;
import com.example.demo.ws.Service.UserService;
import com.example.demo.ws.io.Entity.UserEntity;
import com.example.demo.ws.shared.MyUtils;
import com.example.demo.ws.shared.dto.UserDto;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepo userRepo;

	@Autowired
	MyUtils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto user) {
		UserEntity storeUser = userRepo.findUserByEmail(user.getEmail());
		if(storeUser != null) {
			throw new RuntimeException("Duplicate Recorde");
		}

		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);

		String publicUserId = utils.generateUserId(16);
		userEntity.setUserId(publicUserId);


		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		//save in DB
		UserEntity userEntityDB = userRepo.save(userEntity);

		UserDto userDto = new UserDto();

		BeanUtils.copyProperties(userEntityDB, userDto);

		return userDto;
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepo.findUserByEmail(email);
		if(userEntity == null){
			throw new UsernameNotFoundException(email);
		}
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity,returnValue);
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("----------loadUserByUsername() Method Call----------");
		UserEntity userEntity = userRepo.findUserByEmail(username);
		if(userEntity == null){
			throw new UsernameNotFoundException(username);
		}
		return new User(username,userEntity.getEncryptedPassword(),new ArrayList<>());
	}

}
