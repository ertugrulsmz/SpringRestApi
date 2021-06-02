package com.appsdeveloperblog.app.ws.service.impl;

import com.appsdeveloperblog.app.ws.service.external.EmailConstants;
import com.appsdeveloperblog.app.ws.service.external.EmailService;
import com.appsdeveloperblog.app.ws.service.external.EmailServiceImpl;
import com.appsdeveloperblog.app.ws.service.external.EmailType;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repository.UserRepository;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Utils utils;

    @Autowired
    private BCryptPasswordEncoder bcryptPasswordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public UserDto createUser(UserDto user) {

        if(userRepository.findUserByEmail(user.getEmail()) != null){
            throw new RuntimeException("User with that email exists");
        }

        //Create bidirectional connection and addressID
        for (int i = 0 ; i<user.getAddresses().size();i++){
            AddressDto addressDto = user.getAddresses().get(i);
            addressDto.setUserDetails(user);
            addressDto.setAddressId(utils.generateAddressId(30));
        }

        UserEntity userEntity = new ModelMapper().map(user,UserEntity.class);

        userEntity.setEncryptedPassword(bcryptPasswordEncoder.encode(user.getPassword()));
        String generatedUserId = utils.generateUserId(30);
        userEntity.setUserId(generatedUserId);

        String generatedEmailToken = utils.generateEmailVerificationToken(generatedUserId);
        userEntity.setEmailVerificationToken(generatedEmailToken);
        userEntity.setEmailVerificationStatus(false); // Not confirmed yet.

        // send e-mail
        emailService.
                sendSimpleMessage(userEntity.getEmail(),
                EmailType.VERIFICATION.name(), EmailConstants.getVerificationSubject(generatedEmailToken));

        UserEntity storedUserEntity = userRepository.save(userEntity);

        UserDto returnValue = new ModelMapper().map(storedUserEntity,UserDto.class);

        return returnValue;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findUserByEmail(email);
        if(userEntity == null){
            throw new UsernameNotFoundException(email);
        }

        UserDto returnValue = new ModelMapper().map(userEntity,UserDto.class);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String id) {
        UserEntity userEntity = userRepository.findByUserId(id);

        if(userEntity == null){
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }

        UserDto returnValue = new ModelMapper().map(userEntity,UserDto.class);
        return returnValue;

    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {
        UserEntity userEntity = userRepository.findByUserId(id);

        if (userEntity == null){
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }

        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());

        UserEntity storedEntity = userRepository.save(userEntity);

        UserDto returnValue = new ModelMapper().map(storedEntity,UserDto.class);

        return returnValue;
    }

    @Override
    public void deleteUser(String id) {
        UserEntity userEntity = userRepository.findByUserId(id);

        if(userEntity == null){
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }

        userRepository.delete(userEntity); // delete by id could be preferrred
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        Pageable pageableRequest = PageRequest.of(page,limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);

        List<UserEntity> userContents = usersPage.getContent();

        List<UserDto> returnDtos = new ArrayList<UserDto>();
        for(UserEntity entity : userContents){
            UserDto userDto = new ModelMapper().map(entity,UserDto.class);
            returnDtos.add(userDto);
        }

        return returnDtos;
    }

    @Override
    public boolean verifyEmailToken(String token) {





        boolean returnValue = false;
        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);
        //Normally the user might be checked if he/she is the on who enters it.

        if(userEntity!=null){
            boolean isTokenExpired = Utils.hasTokenExpired(token);

            if(!isTokenExpired){
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(true); // confirmed account does not hold token any longer.
                userRepository.save(userEntity);
                returnValue = true;
            }
        }

        return returnValue;
    }

    // It is method of UserDetailsService which UserService interface extends. This will be triggered when
    // authentication : authenticationmanager.authenticate
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserByEmail(email);

        if(userEntity == null) throw new UsernameNotFoundException(email);
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                userEntity.getEmailVerificationStatus(),
                true,true,true,
                new ArrayList<>());

    }

}
