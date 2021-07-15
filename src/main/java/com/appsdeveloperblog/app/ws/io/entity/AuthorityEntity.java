package com.appsdeveloperblog.app.ws.io.entity;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name="authorities")
public class AuthorityEntity {

    public AuthorityEntity() {
    }

    public AuthorityEntity(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  long id;


    @Column(nullable = false, length = 20)
    private String name;

    //backreference
    @ManyToMany(mappedBy = "authorities") // Collection<AuthorityEntity> authorities
    private Collection<RoleEntity> roles;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Collection<RoleEntity> roles) {
        this.roles = roles;
    }

}
