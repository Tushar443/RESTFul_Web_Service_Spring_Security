package com.example.demo.Controller;

import com.example.demo.ws.ui.model.response.ErrorMessages;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
	@GetMapping(path = "/{id}")
	public UserRest getUser(@PathVariable String id) {
		UserRest userRest = new UserRest();
		UserDto dbValue = userSerivce.getUserByUserId(id);
		BeanUtils.copyProperties(dbValue,userRest);
		return userRest;
	}

//	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE}
//				,produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})

	@PostMapping
	public UserRest addUser(@RequestBody UserDetailsReqModel userDetails) throws Exception {
		UserRest userRest = new UserRest();
		if(userDetails.getFirstName().isEmpty()){
			throw new Exception(ErrorMessages.MESSING_REQUIRED_FIELDS.getErrorMessage());
		}
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
