package com.example.demo.ws.Service.impl;

import com.example.demo.ws.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.UserRepo;
import com.example.demo.ws.Service.UserServiceIfc;
import com.example.demo.ws.io.Entity.UserEntity;
import com.example.demo.ws.shared.MyUtils;
import com.example.demo.ws.shared.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserServiceIfc {

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

	/**
	 * @param id
	 * @return
	 */
	@Override
	public UserDto getUserByUserId(String id) {
		UserEntity userEntity = userRepo.findByUserId(id);
		if(userEntity == null){
			throw new UsernameNotFoundException("User with id = "+id+" not Found");
		}
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity,returnValue);
		return returnValue;
	}

	/**
	 * @param id
	 * @param userDto
	 * @return
	 */
	@Override
	public UserDto updateUser(String id, UserDto userDto) {
		UserEntity userEntity = userRepo.findByUserId(id);
		if(userEntity == null){
			throw new UsernameNotFoundException(id);
		}
		UserDto returnValue = new UserDto();
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		UserEntity updatedUser = userRepo.save(userEntity);
		BeanUtils.copyProperties(updatedUser,returnValue);
		return returnValue;
	}

	/**
	 * @param id
	 * @return
	 */
	@Override
	public UserDto DeleteUser(String id) {
		UserEntity userEntity = userRepo.findByUserId(id);
		if(userEntity == null){
			throw new UsernameNotFoundException(id);
		}
		UserDto returnValue = new UserDto();
		userRepo.delete(userEntity);
		BeanUtils.copyProperties(userEntity,returnValue);
		return returnValue;
	}

	/**
	 * @param page
	 * @param limit
	 * @return
	 */
	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValue = new ArrayList<>();

		Pageable pageable = PageRequest.of(page,limit);

		Page<UserEntity> userPages = userRepo.findAll(pageable);
		List<UserEntity> userEntities = userPages.getContent();
		for (UserEntity userEntity : userEntities){
			UserDto userDto= new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValue.add(userDto);
		}
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
