package com.appsdeveloperblog.app.ws.service;


import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repository.UserRepository;
import com.appsdeveloperblog.app.ws.service.external.EmailService;
import com.appsdeveloperblog.app.ws.service.impl.UserServiceImpl;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bcryptPasswordEncoder;

    @Mock
    EmailService emailService;

    UserEntity user;
    String mockPassword = "axyef2c";
    String mockUserId = "adfnc2";
    String mockEmailToken = "asdfg";

    @BeforeEach
    public void setUp() throws Exception{
        MockitoAnnotations.openMocks(this);

        user = new UserEntity();
        user.setId(1);
        user.setFirstName("ertugrul");
        user.setEmail("ertug@gmail.com");


    }


    @Test
    public void testGetUser(){


        when(userRepository.findUserByEmail(anyString())).thenReturn(user);

        UserDto userDto = userService.getUser("test@test.com");
        assertNotNull(userDto);
        
        assertEquals("ertugrul",userDto.getFirstName());
    }

    @Test
    public void testGetUser_UserNameNotFoundExpection(){
        when(userRepository.findUserByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                ()->{userService.getUser(anyString());});
    }

    @Test
    public void testCreateUser(){

        // add addresses to entity
        user.setAddresses(getAddressEntityList());
        when(userRepository.findUserByEmail(anyString())).thenReturn(user);
        when(bcryptPasswordEncoder.encode(anyString())).thenReturn(mockPassword);
        when(utils.generateUserId(anyInt())).thenReturn(mockUserId);
        when(utils.generateEmailVerificationToken(anyString())).thenReturn(mockEmailToken);
        when(utils.generateAddressId(anyInt())).thenReturn("as");
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);


        UserDto inputDto = getSimpleUserDto();
        inputDto.setAddresses(getAddressDtoList());


        UserDto returnDto = userService.createUser(inputDto);

        assertNotNull(returnDto);
        assertEquals(user.getAddresses().size(),returnDto.getAddresses().size());
        assertEquals(user.getAddresses().get(0).getCity(),returnDto.getAddresses().get(0).getCity());

        verify(bcryptPasswordEncoder,times(1)).encode(anyString());
        verify(utils,times(1)).generateUserId(anyInt());
        verify(userRepository,times(1)).save(any(UserEntity.class));
        verify(utils,times(inputDto.getAddresses().size())).generateAddressId(anyInt());


    }

    private List<AddressDto> getAddressDtoList(){
        AddressDto a1 = new AddressDto();
        a1.setCity("ankara");
        a1.setType("home");
        a1.setPostalCode("asd");
        a1.setStreetName("xy");

        AddressDto a2 = new AddressDto();
        a2.setCity("ankara2");
        a2.setType("home2");
        a2.setPostalCode("asd2");
        a2.setStreetName("xy2");

        List<AddressDto> returnList = new ArrayList<AddressDto>();
        returnList.add(a1);
        returnList.add(a2);

        return returnList;
    }

    private List<AddressEntity> getAddressEntityList(){
        List<AddressDto> addressDtoList = getAddressDtoList();

        Type listType = new TypeToken<List<AddressEntity>>(){}.getType();

        return new ModelMapper().map(addressDtoList,listType);

    }

    private UserDto getSimpleUserDto(){
        UserDto userDto = new UserDto();
        userDto.setPassword(mockPassword);
        userDto.setFirstName("mockname");
        userDto.setLastName("mocksurname");
        userDto.setEmailVerificationStatus(true);

        return userDto;

    }

    public void testCreateUser_UserNotFoundException(){
        when(userRepository.findUserByEmail(anyString())).thenReturn(null);

        UserDto inputDto = getSimpleUserDto();

        assertThrows(RuntimeException.class,() -> userService.createUser(inputDto));
    }
}
