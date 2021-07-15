package com.appsdeveloperblog.app.ws.io.entity;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "roles")
public class RoleEntity {

    public RoleEntity() {
    }

    public RoleEntity(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 20)
    private String name;

    //backreference
    @ManyToMany(mappedBy = "roles") // Collection<RoleEntity> roles
    private Collection<UserEntity> users;

    @ManyToMany(cascade={CascadeType.PERSIST}, fetch=FetchType.EAGER)
    @JoinTable(
            name="roles_authorities",
            joinColumns=@JoinColumn(name = "roles_id", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name="authorities_id", referencedColumnName="id")
    )
    private Collection<AuthorityEntity> authorities;

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

    public Collection<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Collection<UserEntity> users) {
        this.users = users;
    }

    public Collection<AuthorityEntity> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<AuthorityEntity> authorities) {
        this.authorities = authorities;
    }
}
