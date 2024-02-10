package com.example.demo.ws.Service.impl;

import com.example.demo.Repository.PasswordResetTokenRepo;
import com.example.demo.ws.io.Entity.PasswordResetTokenEntity;
import com.example.demo.ws.shared.dto.AddressDTO;
import org.modelmapper.ModelMapper;
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
    PasswordResetTokenRepo passwordResetTokenRepo;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto user) {
		UserEntity storeUser = userRepo.findUserByEmail(user.getEmail());
		ModelMapper mapper = new ModelMapper();
		if(storeUser != null) {
			throw new RuntimeException("Duplicate Recorde");
		}
		for(int i =0 ; i < user.getAddresses().size();i++){
			AddressDTO address = user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i,address);
		}
		UserEntity userEntity = mapper.map(user,UserEntity.class);
		String publicUserId = utils.generateUserId(16);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		String emailVerificationToken = utils.generateEmailVerificationToken(publicUserId);
		userEntity.setEmailVerificationToken(emailVerificationToken);
		userEntity.setEmailVerificationStatus(false);
		//save in DB
		UserEntity userEntityDB = userRepo.save(userEntity);
		UserDto userDto = mapper.map(userEntityDB,UserDto.class);
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

	/**
	 * @param token
	 * @return
	 */
	@Override
	public boolean verifyEmail(String token) {
		boolean isVerify = false;

		//find user by token
		UserEntity userEntity = userRepo.findUserByEmailVerificationToken(token);

		if(userEntity != null){
			boolean hasTokenExpired = MyUtils.hasTokenExpired(token);
			if(!hasTokenExpired){
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepo.save(userEntity);
				isVerify = true;
			}
		}
		return isVerify;
	}

    /**
     * @param email
     * @return
     */
    @Override
    public boolean requestPasswordReset(String email) {
        Boolean result = false;
        UserEntity userEntity = userRepo.findUserByEmail(email);
        if(userEntity == null){
            return false;
        }
        String token = utils.generatePasswordRestToken(userEntity.getUserId());

        PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
        passwordResetTokenEntity.setToken(token);
        passwordResetTokenEntity.setUserDetails(userEntity);
        PasswordResetTokenEntity returnValue = passwordResetTokenRepo.save(passwordResetTokenEntity);
        if(returnValue != null){
            result = true;
        }
        return result;
    }

    /**
     * @param password
     * @param token
     * @return
     */
    @Override
    public Boolean resetPassword(String password, String token) {
        Boolean returnValue = false;
        if(MyUtils.hasTokenExpired(token)){
            return returnValue;
        }

        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepo.findByToken(token);
        if(passwordResetTokenEntity == null){
            return returnValue;
        }

        String encodedPass = bCryptPasswordEncoder.encode(password);

        UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
        userEntity.setEncryptedPassword(encodedPass);
        UserEntity saveUser = userRepo.save(userEntity);
        if(saveUser != null && saveUser.getEncryptedPassword().equals(encodedPass)){
            returnValue = true;
        }
        passwordResetTokenRepo.delete(passwordResetTokenEntity);
        return returnValue;
    }

    @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("----------loadUserByUsername() Method Call----------");
		UserEntity userEntity = userRepo.findUserByEmail(username);
		if(userEntity == null){
			throw new UsernameNotFoundException(username);
		}
		//return new User(username,userEntity.getEncryptedPassword(),new ArrayList<>());
		return new User(userEntity.getEmail(),userEntity.getEncryptedPassword()
				,userEntity.getEmailVerificationStatus()
				,true,true,true,new ArrayList<>());
	}
}
