package com.appsdeveloperblog.app.ws.security;

import com.appsdeveloperblog.app.ws.io.entity.AuthorityEntity;
import com.appsdeveloperblog.app.ws.io.entity.RoleEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyCustomUserDetails implements UserDetails {

    private UserEntity userEntity;
    private String userId;

    public MyCustomUserDetails() {
    }

    public MyCustomUserDetails(UserEntity userEntity){
        this.userEntity = userEntity;
        this.userId = userEntity.getUserId();
    }

    /*
    Add both names of  roleentities and authorityentities by SimpleAuthority(x.getName())
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<AuthorityEntity> authorityEntities = new ArrayList<>();

        Collection<RoleEntity> roles = this.userEntity.getRoles();

        if(roles == null) return authorities;

        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            authorityEntities.addAll(role.getAuthorities());
        });

        authorityEntities.forEach(authority -> {
            authorities.add(new SimpleGrantedAuthority(authority.getName()));
        });

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.userEntity.getEncryptedPassword();
    }

    @Override
    public String getUsername() {
        return this.userEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.userEntity.getEmailVerificationStatus();
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
