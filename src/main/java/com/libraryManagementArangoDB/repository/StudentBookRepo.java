package com.libraryManagementArangoDB.repository;

import java.util.Optional;

import com.arangodb.springframework.repository.ArangoRepository;
import com.libraryManagementArangoDB.model.StudentBookCollection;

public interface StudentBookRepo extends ArangoRepository<StudentBookCollection, String> {

    // Optional<StudentBookCollection> findByBookIdAndUserId(String bookId, String
    // userId);

}
