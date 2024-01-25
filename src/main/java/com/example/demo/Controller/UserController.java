package com.example.demo.Controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.ws.Service.UserService;
import com.example.demo.ws.shared.dto.UserDto;
import com.example.demo.ws.ui.model.request.UserDetailsReqModel;
import com.example.demo.ws.ui.model.response.UserRest;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserService userSerivce;

//	@GetMapping("/")
	@GetMapping
	public String getUser() {
		System.out.println("Call Hello service");
		return "Hello World!!!";
	}

	@PostMapping
	public UserRest addUser(@RequestBody UserDetailsReqModel userDetails) {
		UserRest userRest = new UserRest();
		UserDto userDto = new UserDto();
		System.out.println("Call create User");
		BeanUtils.copyProperties(userDetails, userDto);
		UserDto createUser = userSerivce.createUser(userDto);
		BeanUtils.copyProperties(createUser, userRest);
		return userRest;
	}

	@DeleteMapping
	public String deleteUser() {
		return "delete user called";
	}

	@PutMapping
	public String updateUser() {
		return "update user called";
	}
}
