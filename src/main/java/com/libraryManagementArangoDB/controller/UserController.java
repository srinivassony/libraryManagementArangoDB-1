package com.libraryManagementArangoDB.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/v2")
public class UserController {

    // @PostMapping("/get/all/books")
    // @PreAuthorize("hasRole('ROLE_STUDENT')or hasRole('ROLE_ADMIN')")
    // public ResponseEntity<?> getAllBooks(HttpServletRequest req,
    // HttpServletResponse res, @RequestParam String searchKey) {
    // return userservices.getAllBooks(req, res, searchKey);
    // }

}
