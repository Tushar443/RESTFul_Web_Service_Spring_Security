package com.example.demo.ws.Service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.demo.ws.shared.dto.UserDto;
@Service
public interface UserService extends UserDetailsService{

	UserDto createUser(UserDto user);

	UserDto getUser(String email);
}
