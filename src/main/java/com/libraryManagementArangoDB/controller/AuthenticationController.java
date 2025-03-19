package com.libraryManagementArangoDB.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.libraryManagementArangoDB.config.CustomResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {

    @GetMapping("/add/user")
    public ResponseEntity<?> addUser(HttpServletRequest req, HttpServletResponse res) {
        CustomResponse<String> responseBody = new CustomResponse<>("User added sucessfully", "CREATED",
                HttpStatus.OK.value(),
                req.getRequestURI(), LocalDateTime.now());
        System.out.println(responseBody.toString());
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
