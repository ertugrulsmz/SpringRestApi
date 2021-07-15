package com.appsdeveloperblog.app.ws.io.repository;

import com.appsdeveloperblog.app.ws.io.entity.AuthorityEntity;
import com.appsdeveloperblog.app.ws.io.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends CrudRepository<AuthorityEntity,Long> {
    public AuthorityEntity findByName(String name);
}
