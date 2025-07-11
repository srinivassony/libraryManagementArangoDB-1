package com.libraryManagementArangoDB.repository;

import org.springframework.stereotype.Repository;

import com.arangodb.springframework.repository.ArangoRepository;
import com.libraryManagementArangoDB.model.BookCollection;

@Repository
public interface BookRepo extends ArangoRepository<BookCollection, String> {

}
