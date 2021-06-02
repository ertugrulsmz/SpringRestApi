package com.appsdeveloperblog.app.ws.controller;


import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.SpringVersion;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @PostMapping(produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE},
    consumes = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails)  {

        UserDto userDto = new ModelMapper().map(userDetails,UserDto.class);

        UserDto createdUser = userService.createUser(userDto);

        UserRest returnValue = new ModelMapper().map(createdUser,UserRest.class);

        return returnValue;
    }

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id)
    {
        UserDto userDto = userService.getUserByUserId(id);

        UserRest returnValue = new ModelMapper().map(userDto,UserRest.class);

        return returnValue;

    }

    @PutMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@PathVariable String id,@RequestBody UserDetailsRequestModel userDetails)  {

        UserDto userDto = new ModelMapper().map(userDetails,UserDto.class);

        UserDto updatedUser = userService.updateUser(id,userDto);

        UserRest returnValue = new ModelMapper().map(updatedUser,UserRest.class);

        return returnValue;


    }

    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE}
           )
    public OperationStatusModel deleteUser(@PathVariable String id){

        OperationStatusModel operation = new OperationStatusModel();
        operation.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        operation.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return operation;
    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value="page",defaultValue = "0") int page,
                                   @RequestParam(value="limit",defaultValue="25") int limit){

        List<UserRest> returnList = new ArrayList();

        List<UserDto> userListFromService = userService.getUsers(page,limit);

        for(UserDto u : userListFromService){

            UserRest userRest = new ModelMapper().map(u,UserRest.class);

           returnList.add(userRest);
        }

        return returnList;
    }


    @GetMapping(path = "/{id}/addresses", produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE}
    )
    public CollectionModel<AddressRest> getUserAddresses(@PathVariable String id){

        List<AddressDto>  userAddresses = addressService.getAddresses(id);

        List<AddressRest> returnValues = null;


        if(userAddresses!=null && !userAddresses.isEmpty()){
            // we could do conversion by one by with for loop
            java.lang.reflect.Type restListtype = new TypeToken<List<AddressRest>>() {}.getType();
            returnValues = new ModelMapper().map(userAddresses,restListtype);

            for(AddressRest r : returnValues){

                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                .getUserAddress(id,r.getAddressId())).withSelfRel();
                r.add(selfLink);
            }
        }

        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                .getUserAddresses(id)).withSelfRel();




        return CollectionModel.of(returnValues,userLink,selfLink);
    }

    @GetMapping(path = "/{id}/addresses/{addressId}", produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE}
    )
    public EntityModel<AddressRest> getUserAddress(@PathVariable String addressId,
                                      @PathVariable String id){
        // we are not using first id which is userId public.

        AddressDto addressDto = addressService.getAddress(addressId);
        AddressRest returnValue = new ModelMapper().map(addressDto,AddressRest.class);

        //http://localhost:8080/users/{id}/addresses/{addressId}
        // This is one way
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");

        // Secondly
        Link userAddressLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
        .getUserAddresses(id)).withRel("addresses");

        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                .getUserAddress(id,addressId)).withSelfRel();



        //Collection.of is used at getAdresses method as we return list at upper method
        return EntityModel.of(returnValue, Arrays.asList(userLink,userAddressLink,selfLink));

    }

    @GetMapping(path="email-verification", produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel verifyEmailToken(@RequestParam(value="token") String token){
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name()); // it requires string
        // as it will be returned as json. But string is given by enum

        boolean isVerified = userService.verifyEmailToken(token);

        if(isVerified){
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }else{
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return returnValue;

    }






}
