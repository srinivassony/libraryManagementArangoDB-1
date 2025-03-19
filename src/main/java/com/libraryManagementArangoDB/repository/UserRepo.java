package com.libraryManagementArangoDB.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.arangodb.springframework.repository.ArangoRepository;
import com.libraryManagementArangoDB.model.UserCollection;

@Repository
public interface UserRepo extends ArangoRepository<UserCollection, String> {

    Optional<UserCollection> findByEmail(String email);

}
