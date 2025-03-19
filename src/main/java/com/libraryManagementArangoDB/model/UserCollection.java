package com.libraryManagementArangoDB.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.PersistentIndexed;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "lm_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCollection {

    @Id
    @PersistentIndexed(unique = true)
    private String id;

    private String userName;

    @PersistentIndexed(unique = true)
    private String email;

    private String password;

    private String role;

    @PersistentIndexed(unique = true)
    private String uuid;

    private String phone;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private String dob;

    @PersistentIndexed(unique = true)
    private String rollNo;

    private String country;

    private String state;

    private String gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private String updatedBy;
}
