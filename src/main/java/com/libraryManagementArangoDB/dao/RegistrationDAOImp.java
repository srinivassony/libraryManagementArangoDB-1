package com.libraryManagementArangoDB.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.libraryManagementArangoDB.model.UserCollection;
import com.libraryManagementArangoDB.repository.UserRepo;

@Service
public class RegistrationDAOImp implements RegistrationDAO {

    @Autowired
    UserRepo userRepo;

    @Override
    public Optional<UserCollection> isUserExists(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public UserCollection createUser(UserCollection userDetails) {
        return userRepo.save(userDetails);
    }

}
