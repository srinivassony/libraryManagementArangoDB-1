package com.libraryManagementArangoDB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.arangodb.springframework.annotation.EnableArangoRepositories;

@SpringBootApplication
public class LibraryManagementArangoDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryManagementArangoDbApplication.class, args);
	}

}
