package com.appsdeveloperblog.app.ws.service.impl;

import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repository.AddressRepository;
import com.appsdeveloperblog.app.ws.io.repository.UserRepository;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public List<AddressDto> getAddresses(String id) {

        UserEntity userEntity = userRepository.findByUserId(id);
        // we could directly say userEntity.getAddresses but we want to use addressRepository

        // I could have said List, we already return List but iterable provide enough
        Iterable<AddressEntity> addressEntities = addressRepository.findAllByUserDetails(userEntity);
        List<AddressDto> returnAddresses = new ArrayList<AddressDto>();


        for(AddressEntity a : addressEntities){
            returnAddresses.add(new ModelMapper().map(a,AddressDto.class));
        }

        return returnAddresses;
    }

    @Override
    public AddressDto getAddress(String id) {
        AddressEntity addressEntity = addressRepository.findByAddressId(id);

        AddressDto returnValue = null;

        if (addressEntity!=null) // probably modelmapper throws error
            returnValue = new ModelMapper().map(addressEntity,AddressDto.class);

        return returnValue;
    }

}
