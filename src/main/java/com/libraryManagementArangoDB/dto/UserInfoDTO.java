package com.libraryManagementArangoDB.dto;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.libraryManagementArangoDB.model.UserCollection;

public class UserInfoDTO implements UserDetails {

    private String id;
    private String userName;
    private String email;
    private String password;
    private List<GrantedAuthority> authorities;
    private String uuid;

    public UserInfoDTO(UserCollection userCollection) {
        super();
        this.id = userCollection.getId();
        this.userName = userCollection.getUserName();
        this.email = userCollection.getEmail();
        this.password = userCollection.getPassword();
        this.authorities = Arrays.stream(userCollection.getRole().split(",")).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        this.uuid = userCollection.getUuid();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return authorities;
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return password;
    }

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return email;
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
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
