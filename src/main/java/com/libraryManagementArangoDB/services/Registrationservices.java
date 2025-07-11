package com.libraryManagementArangoDB.services;

import org.springframework.http.ResponseEntity;

import com.libraryManagementArangoDB.dto.UserServiceDTO;
import com.libraryManagementArangoDB.model.UserCollection;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Registrationservices {

    ResponseEntity<?> createUserInfo(UserServiceDTO userservicesDTO, HttpServletRequest req,
            HttpServletResponse res);

    ResponseEntity<?> authenticateUser(UserCollection userCollection, HttpServletRequest req, HttpServletResponse res);

}
