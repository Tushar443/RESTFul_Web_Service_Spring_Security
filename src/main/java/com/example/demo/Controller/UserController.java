package com.example.demo.Controller;

import com.example.demo.ws.Service.AddressServiceIfc;
import com.example.demo.ws.exception.UserServiceException;
import com.example.demo.ws.shared.dto.AddressDTO;
import com.example.demo.ws.ui.model.response.AddressResponseModel;
import com.example.demo.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.internal.bytebuddy.description.method.MethodDescription;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.example.demo.ws.Service.UserServiceIfc;
import com.example.demo.ws.shared.dto.UserDto;
import com.example.demo.ws.ui.model.request.UserDetailsReqModel;
import com.example.demo.ws.ui.model.response.UserRest;

import java.lang.reflect.Type;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserServiceIfc userSerivce;

	@Autowired
	AddressServiceIfc addressServiceIfc;

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

	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE}
				,produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public UserRest addUser(@RequestBody UserDetailsReqModel userDetails) throws Exception {
		if(userDetails.getFirstName().isEmpty()){
//			throw new Exception(ErrorMessages.MESSING_REQUIRED_FIELDS.getErrorMessage());
			throw new UserServiceException(ErrorMessages.MESSING_REQUIRED_FIELDS.getErrorMessage());
		}
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails,UserDto.class);
		UserDto createUser = userSerivce.createUser(userDto);
		UserRest userRest = modelMapper.map(userDto,UserRest.class);
		return userRest;
	}

	@DeleteMapping(path = "/{id}")
	public UserRest deleteUser(@PathVariable String id) {
        UserRest userRest = new UserRest();
        System.out.println("Call Delete User");
        UserDto deletedUser = userSerivce.DeleteUser(id);
        BeanUtils.copyProperties(deletedUser, userRest);
        return userRest;
	}

	@PutMapping(path = "/{id}")
	public UserRest updateUser(@PathVariable String id,@RequestBody UserDetailsReqModel userDetails) {
		UserRest userRest = new UserRest();
		if(userDetails.getFirstName().isEmpty()){
			throw new UserServiceException(ErrorMessages.MESSING_REQUIRED_FIELDS.getErrorMessage());
		}
		UserDto userDto = new UserDto();
		System.out.println("Call Update User");
		BeanUtils.copyProperties(userDetails, userDto);
		UserDto updateUser = userSerivce.updateUser(id ,userDto);
		BeanUtils.copyProperties(updateUser, userRest);
		return userRest;
	}

    @GetMapping
    public List<UserRest> getUserByCondition(@RequestParam(value ="page" ,defaultValue = "0") int page,
                                             @RequestParam(value ="limit" ,defaultValue = "25") int limit){
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userSerivce.getUsers(page,limit);

        for (UserDto userDto : users){
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }

        return returnValue;
    }

	@GetMapping(path = "/{id}/addresses")
	public List<AddressResponseModel> getAddressByUserId(@PathVariable String id) {
		List<AddressResponseModel> returnValue = new ArrayList<>();
		List<AddressDTO> addressDTOList = addressServiceIfc.getAddresses(id);
		ModelMapper modelMapper = new ModelMapper();
		if(addressDTOList != null && !addressDTOList.isEmpty()) {
			Type listType = new TypeToken<List<AddressResponseModel>>() {}.getType();
			returnValue = modelMapper.map(addressDTOList, listType);
		}
		return returnValue;
	}

	@GetMapping(path = "/{id}/addresses/{addressId}")
	public AddressResponseModel getAddressByAddressId(@PathVariable String addressId) {
		AddressResponseModel returnValue = new AddressResponseModel();
		AddressDTO addressDTO = addressServiceIfc.getAddress(addressId);
		ModelMapper modelMapper = new ModelMapper();
		if(addressDTO==null){return returnValue;}
		returnValue = modelMapper.map(addressDTO, AddressResponseModel.class);
		return returnValue;
	}
}
