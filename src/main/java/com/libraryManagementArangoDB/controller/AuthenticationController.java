package com.libraryManagementArangoDB.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.libraryManagementArangoDB.config.CustomResponse;
import com.libraryManagementArangoDB.dto.UserServiceDTO;
import com.libraryManagementArangoDB.services.RegistrationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {

    @Autowired
    RegistrationService registrationService;

    @PostMapping("/add/user")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserServiceDTO userServiceDTO, BindingResult result,
            HttpServletRequest req, HttpServletResponse res) {

        if (result.hasErrors()) {

            // Collecting error messages
            StringBuilder errorMessages = new StringBuilder();

            result.getAllErrors().forEach(error -> errorMessages.append(error.getDefaultMessage()).append("; "));

            System.out.println("errorMessages" + " " + errorMessages);

            CustomResponse<String> responseBody = new CustomResponse<>(errorMessages.toString(), "BAD_REQUEST",
                    HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        return registrationService.createUserInfo(userServiceDTO, req, res);
    }

}
