package com.appsdeveloperblog.app.ws;

import com.appsdeveloperblog.app.ws.io.entity.AuthorityEntity;
import com.appsdeveloperblog.app.ws.io.entity.RoleEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repository.AuthorityRepository;
import com.appsdeveloperblog.app.ws.io.repository.RoleRepository;
import com.appsdeveloperblog.app.ws.io.repository.UserRepository;
import com.appsdeveloperblog.app.ws.shared.AuthorityNames;
import com.appsdeveloperblog.app.ws.shared.RoleNames;
import com.appsdeveloperblog.app.ws.shared.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;

@Component
public class InitialUserSetup {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private Utils utils;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent){
        AuthorityEntity readAuthority = createAuthorityEntity(AuthorityNames.READ_AUTHORITY.name());
        AuthorityEntity writeAuthority = createAuthorityEntity(AuthorityNames.WRITE_AUTHORITY.name());
        AuthorityEntity deleteAuthority = createAuthorityEntity(AuthorityNames.DELETE_AUTHORITY.name());

        RoleEntity roleUser = createRole(RoleNames.ROLE_USER.name(), Arrays.asList(readAuthority,writeAuthority));
        RoleEntity roleAdmin = createRole(RoleNames.ROLE_ADMIN.name(), Arrays.asList(readAuthority,writeAuthority,deleteAuthority));

        if(userRepository.findUserByEmail("ertugrulsmz59@gmail.com") != null) return;

        UserEntity admin = new UserEntity();
        admin.setEmail("ertugrulsmz59@gmail.com");
        admin.setFirstName("ertugrul");
        admin.setLastName("semiz");
        admin.setEmailVerificationStatus(true);
        admin.setEncryptedPassword(bCryptPasswordEncoder.encode("asdsa"));
        admin.setUserId(utils.generateUserId(30));
        admin.setRoles(Arrays.asList(roleAdmin));

        userRepository.save(admin);

    }

    @Transactional
    protected AuthorityEntity createAuthorityEntity(String name){
        AuthorityEntity authorityEntity = authorityRepository.findByName(name);

        if(authorityEntity == null){
            return authorityRepository.save(new AuthorityEntity(name));
        }

        return authorityEntity;
    }

    @Transactional
    protected RoleEntity createRole(String name, Collection<AuthorityEntity> authorities){
        RoleEntity roleEntity = roleRepository.findByName(name);

        if(roleEntity == null){
            RoleEntity role = new RoleEntity(name);
            role.setAuthorities(authorities);
            return roleRepository.save(role);
        }

        return roleEntity;

    }
}
