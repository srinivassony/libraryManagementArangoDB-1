package com.libraryManagementArangoDB.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.arangodb.springframework.repository.ArangoRepository;
import com.libraryManagementArangoDB.model.UserCollection;

@Repository
public interface UserRepo extends ArangoRepository<UserCollection, String> {

    Optional<UserCollection> findByEmail(String email);

    Page<UserCollection> findByRole(String role, Pageable pageable);

    List<UserCollection> findUsersByEmail(List<String> emails);

    Optional<UserCollection> findByRollNo(String rollnumber);

}
