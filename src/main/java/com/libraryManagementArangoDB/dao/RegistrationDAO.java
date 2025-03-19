package com.libraryManagementArangoDB.dao;

import java.util.Optional;

import com.libraryManagementArangoDB.model.UserCollection;

public interface RegistrationDAO {

    Optional<UserCollection> isUserExists(String username);

}
