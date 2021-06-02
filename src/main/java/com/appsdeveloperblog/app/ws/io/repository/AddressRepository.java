package com.appsdeveloperblog.app.ws.io.repository;

import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
    // Select by..x x is field of AddressRepository
    // we want all not one so findAll
    List<AddressEntity> findAllByUserDetails(UserEntity user);
    AddressEntity findByAddressId(String id);
}
