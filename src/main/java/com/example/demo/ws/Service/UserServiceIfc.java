package com.example.demo.ws.Service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.demo.ws.shared.dto.UserDto;

import java.util.List;

@Service
public interface UserServiceIfc extends UserDetailsService{

	UserDto createUser(UserDto user);

	UserDto getUser(String email);

    UserDto getUserByUserId(String id);

	UserDto updateUser(String id, UserDto userDto);

	UserDto DeleteUser(String id);

	List<UserDto> getUsers(int page, int limit);

    boolean verifyEmail(String token);

	boolean requestPasswordReset(String email);

	Boolean resetPassword(String password, String token);
}
