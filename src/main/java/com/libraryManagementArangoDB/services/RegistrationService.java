package com.libraryManagementArangoDB.services;

import org.springframework.http.ResponseEntity;

import com.libraryManagementArangoDB.dto.UserServiceDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface RegistrationService {

    ResponseEntity<?> createUserInfo(UserServiceDTO userServiceDTO, HttpServletRequest req,
            HttpServletResponse res);

}
