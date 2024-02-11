package com.example.demo.ws.Service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.Repository.PasswordResetTokenRepo;
import com.example.demo.Repository.UserRepo;
import com.example.demo.ws.io.Entity.UserEntity;
import com.example.demo.ws.shared.MyUtils;
import com.example.demo.ws.shared.dto.AddressDTO;
import com.example.demo.ws.shared.dto.UserDto;

class UserServiceImpleTest {
	@InjectMocks
	UserServiceImpl userService;
	
	@Mock
	UserRepo userRepo;
	
	@Mock
	MyUtils utils;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;


	UserEntity userEntity;
	
	String userId = "dwede3rwrewfr445ed";
	String password = "sdsefderf3fsefcsfrfrsds";


	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("tushar");
		userEntity.setLastName("More1");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(password);
		userEntity.setEmail("test@gmail.com");
		userEntity.setEmailVerificationToken("sefrdfe4r4r4gver");
	}

	@Test
	void testGetUser() {
		when(userRepo.findUserByEmail(anyString())).thenReturn(userEntity);
		UserDto userDto = userService.getUser("test@gmail.com");
		assertNotNull(userDto);
		assertEquals("tushar",userDto.getFirstName());
	}
	
	@Test
	final void testGetUser_UserNotFoundException() {
		when(userRepo.findUserByEmail(anyString())).thenReturn(null);
		
		assertThrows(UsernameNotFoundException.class,
				()->{
					userService
					.getUser("tushar@gmail.com");
				}
				);
	}
	
	@Test
	final void testCreateUser() {
		when(userRepo.findUserByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("edewfdew3r3d344");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(password);
		when(userRepo.save(any(UserEntity.class))).thenReturn(userEntity);
		AddressDTO addresDto = new AddressDTO();
		addresDto.setType("shipping");
		List<AddressDTO> addresses = new ArrayList<AddressDTO>();
		addresses.add(addresDto);
		
		UserDto userDto = new UserDto();
		userDto.setAddresses(addresses);
		UserDto dbUserDto = userService.createUser(userDto);
		assertNotNull(dbUserDto);
		assertEquals(userEntity.getFirstName(),dbUserDto.getFirstName());
	}

}
