package com.example.demo.Controller;

import com.example.demo.ws.ui.model.Service.AddressServiceIfc;
import com.example.demo.ws.ui.model.Service.UserServiceIfc;
import com.example.demo.ws.exception.UserServiceException;
import com.example.demo.ws.shared.Roles;
import com.example.demo.ws.shared.dto.AddressDTO;
import com.example.demo.ws.shared.dto.UserDto;
import com.example.demo.ws.ui.model.request.PasswordResetModel;
import com.example.demo.ws.ui.model.request.PasswordResetRequestModel;
import com.example.demo.ws.ui.model.request.UserDetailsReqModel;
import com.example.demo.ws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/users")
//@Secured("ROLE_ADMIN")
public class UserController {

    @Autowired
    UserServiceIfc userSerivce;

    @Autowired
    AddressServiceIfc addressServiceIfc;

    @PostAuthorize("returnObject.userId == principal.userId")
    //@PreAuthorize("permitAll")
    @GetMapping(path = "/{id}")
    public UserRest getUser(@PathVariable String email) {
        UserRest userRest = new UserRest();
        UserDto dbValue = userSerivce.getUserByUserId(email);
        BeanUtils.copyProperties(dbValue, userRest);
        return userRest;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
            , produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest addUser(@RequestBody UserDetailsReqModel userDetails) throws Exception {
        if (userDetails.getFirstName().isEmpty()) {
//			throw new Exception(ErrorMessages.MESSING_REQUIRED_FIELDS.getErrorMessage());
            throw new UserServiceException(ErrorMessages.MESSING_REQUIRED_FIELDS.getErrorMessage());
        }
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name())));
        UserDto createUser = userSerivce.createUser(userDto);
        UserRest userRest = modelMapper.map(userDto, UserRest.class);
        return userRest;
    }

    //    @Secured("ROLE_ADMIN")
    @PreAuthorize("hasAuthority('DELETE_AUTHORITY') or returnObject.userId == principal.userId")
    @DeleteMapping(path = "/{id}")
    public UserRest deleteUser(@PathVariable String id) {
        UserRest userRest = new UserRest();
        System.out.println("Call Delete User");
        UserDto deletedUser = userSerivce.DeleteUser(id);
        BeanUtils.copyProperties(deletedUser, userRest);
        return userRest;
    }

    @PutMapping(path = "/{id}")
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsReqModel userDetails) {
        UserRest userRest = new UserRest();
        if (userDetails.getFirstName().isEmpty()) {
            throw new UserServiceException(ErrorMessages.MESSING_REQUIRED_FIELDS.getErrorMessage());
        }
        UserDto userDto = new UserDto();
        System.out.println("Call Update User");
        BeanUtils.copyProperties(userDetails, userDto);
        UserDto updateUser = userSerivce.updateUser(id, userDto);
        BeanUtils.copyProperties(updateUser, userRest);
        return userRest;
    }

    @GetMapping
    public List<UserRest> getUserByCondition(@RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> returnValue = new ArrayList<>();
        List<UserDto> users = userSerivce.getUsers(page, limit);
        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }
        return returnValue;
    }

    @GetMapping(path = "/{id}/addresses")
    public CollectionModel<AddressResponseModel> getAddressByUserId(@PathVariable String id) {
        List<AddressResponseModel> returnValue = new ArrayList<>();
        List<AddressDTO> addressDTOList = addressServiceIfc.getAddresses(id);
        ModelMapper modelMapper = new ModelMapper();
        if (addressDTOList != null && !addressDTOList.isEmpty()) {
            Type listType = new TypeToken<List<AddressResponseModel>>() {
            }.getType();
            returnValue = modelMapper.map(addressDTOList, listType);
        }
        for (AddressResponseModel address : returnValue) {
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                            .getAddressByAddressId(address.getAddressId(), id))
                    .withSelfRel();
            address.add(selfLink);
        }
        //Add Links http://localhost:8080/users/<userId>
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");

        //Add Links http://localhost:8080/users/<userId>/addresses/<addressId>
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                        .getAddressByUserId(id))
                .withSelfRel();
        return CollectionModel.of(returnValue, userLink, selfLink);
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}")
    public EntityModel<AddressResponseModel> getAddressByAddressId(@PathVariable String addressId, @PathVariable String userId) {
        AddressResponseModel returnValue = new AddressResponseModel();
        AddressDTO addressDTO = addressServiceIfc.getAddress(addressId);
        ModelMapper modelMapper = new ModelMapper();
        if (addressDTO != null) {
            returnValue = modelMapper.map(addressDTO, AddressResponseModel.class);
        }
        //Add Links http://localhost:8080/users/<userId>
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        //Add Links http://localhost:8080/users/<userId>/addresses
        Link addressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                        .getAddressByUserId(userId))
                .withRel("addresses");
        //Add Links http://localhost:8080/users/<userId>/addresses/<addressId>
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                        .getAddressByAddressId(addressId, userId))
                .withSelfRel();
        return EntityModel.of(returnValue, Arrays.asList(userLink, addressesLink, selfLink));
    }

    @GetMapping(path = "/email-verification")
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
        boolean verified = userSerivce.verifyEmail(token);
        if (verified) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }
        return returnValue;
    }

    @PostMapping(path = "/password-reset-request")
    public OperationStatusModel requestResetPassword(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        OperationStatusModel result = new OperationStatusModel();
        boolean operationResult = userSerivce.requestPasswordReset(passwordResetRequestModel.getEmail());

        result.setOperationResult(RequestOperationStatus.ERROR.name());
        result.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());

        if (operationResult) {
            result.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }
        return result;
    }

    @PostMapping(path = "/password-reset")
    public OperationStatusModel saveNewPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel result = new OperationStatusModel();
        Boolean operationResult = userSerivce.resetPassword(passwordResetModel.getPassword(), passwordResetModel.getToken());
        result.setOperationResult(RequestOperationStatus.ERROR.name());
        result.setOperationName(RequestOperationName.RESET_PASSWORD.name());
        if (operationResult) {
            result.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }
        return result;
    }
}
