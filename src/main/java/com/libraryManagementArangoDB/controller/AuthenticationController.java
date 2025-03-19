package com.libraryManagementArangoDB.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.libraryManagementArangoDB.config.CustomResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {

    @PostMapping("/add/user")
    public ResponseEntity<String> addUser(HttpServletRequest req, HttpServletResponse res) {
        CustomResponse<String> responseBody = new CustomResponse<>("userInfo", "CREATED", HttpStatus.OK.value(),
                req.getRequestURI(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody.toString());
    }

}
