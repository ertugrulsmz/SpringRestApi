package com.appsdeveloperblog.app.ws.controller;

import com.appsdeveloperblog.app.ws.controller.UserController;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.AddressRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private UserDto userDto;
    private UserDetailsRequestModel userDetailsRequestModel;

    private final String mockUserId = "axaRfsaw3";

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);

        userDto = new UserDto();
        userDto.setFirstName("ertug");
        userDto.setLastName("semiz");
        userDto.setPassword("axx");
        userDto.setEmail("ert@gmail.com");
        userDto.setAddresses(getAddressDtoList());
        userDto.setUserId(mockUserId);

        // Example Request Model
        userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setEmail("req@reg.com");
        userDetailsRequestModel.setPassword("asx");
        userDetailsRequestModel.setFirstName("ali");
        userDetailsRequestModel.setLastName("akman");

        List<AddressRequestModel> addresses = new ArrayList<>();
        AddressRequestModel addressRequestModel = new AddressRequestModel();
        addressRequestModel.setCity("bagdat");
        addressRequestModel.setCountry("iran");
        addressRequestModel.setPostalCode("as");
        addresses.add(addressRequestModel);

        userDetailsRequestModel.setAddresses(addresses);


    }

    @Test
    public void testGetUser(){
        when(userService.getUserByUserId(any(String.class))).thenReturn(userDto);

        UserRest userRest = userController.getUser(mockUserId);

        assertNotNull(userRest);
        assertEquals(mockUserId,userRest.getUserId());
        assertEquals(userDto.getAddresses().size(),userRest.getAddresses().size());
        assertEquals(userDto.getFirstName(),userRest.getFirstName());

    }

    @Test
    public void testCreateUser(){

        UserDto userDto = new ModelMapper().map(userDetailsRequestModel,UserDto.class);
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        UserRest userRest = userController.createUser(userDetailsRequestModel);

        assertNotNull(userRest);
        assertEquals(userDetailsRequestModel.getFirstName(),userRest.getFirstName());
        assertEquals(userDetailsRequestModel.getAddresses().size(),userRest.getAddresses().size());
        assertEquals(userDetailsRequestModel.getAddresses().get(0).getCity(),
                userRest.getAddresses().get(0).getCity());


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


}
