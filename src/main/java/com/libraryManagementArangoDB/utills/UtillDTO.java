package com.libraryManagementArangoDB.utills;

import org.springframework.stereotype.Component;

import com.libraryManagementArangoDB.dto.BookservicesDTO;
import com.libraryManagementArangoDB.dto.UserServiceDTO;
import com.libraryManagementArangoDB.model.BookCollection;
import com.libraryManagementArangoDB.model.UserCollection;

@Component
public class UtillDTO {

    public UserServiceDTO convertToUserDTO(UserCollection userCollection) {
        return new UserServiceDTO(
                userCollection.getId(),
                userCollection.getUserName(),
                userCollection.getEmail(),
                userCollection.getRole(),
                userCollection.getCountry(),
                userCollection.getState(),
                userCollection.getDob(),
                userCollection.getPhone(),
                userCollection.getRollNo(),
                userCollection.getGender(),
                null, null, userCollection.getCreatedAt(),
                userCollection.getCreatedBy(),
                null, userCollection.getUpdatedAt(),
                userCollection.getUpdatedBy());
    }

    public BookservicesDTO convertToBookDTO(BookCollection bookEntity) {
        return new BookservicesDTO(
                bookEntity.getId(),
                bookEntity.getBookName(),
                bookEntity.getAuthor(),
                bookEntity.getDescription(),
                bookEntity.getNoOfSets(),
                bookEntity.getCreatedAt(),
                bookEntity.getCreatedBy(),
                bookEntity.getUpdatedAt(),
                bookEntity.getUpdatedBy());
    }
}
