package com.libraryManagementArangoDB.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.libraryManagementArangoDB.dto.BookservicesDTO;
import com.libraryManagementArangoDB.dto.StudentBookDTO;
import com.libraryManagementArangoDB.dto.UserInfoDTO;
import com.libraryManagementArangoDB.dto.UserServiceDTO;
import com.libraryManagementArangoDB.services.Adminservices;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v3")
public class AdminController {

    @Autowired
    Adminservices adminservices;

    @GetMapping("/get/student/role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserByRoles(HttpServletRequest req,
            HttpServletResponse res, @RequestParam int page, @RequestParam int size) {

        return adminservices.getStudentRole(req, res, page, size);
    }

    @PostMapping("/upload/bulk/books")
    public ResponseEntity<?> uploadBooksData(HttpServletRequest req,
            HttpServletResponse res,
            @RequestParam("file") MultipartFile file) {

        UserInfoDTO userDetails = (UserInfoDTO) req.getAttribute("user");

        return adminservices.uploadBooksData(req, res, file, userDetails);
    }

    @PostMapping("/update/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUserById(HttpServletRequest req,
            HttpServletResponse res, @PathVariable("id") String id, @RequestBody UserServiceDTO userservicesDTO) {
        UserInfoDTO userDetails = (UserInfoDTO) req.getAttribute("user");

        System.out.println("USER DETAILS" + " " + userDetails);
        return adminservices.updateUserById(req, res, id, userservicesDTO, userDetails);
    }

    @DeleteMapping("/delete/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(HttpServletRequest req, HttpServletResponse res,
            @PathVariable("id") String id) {

        return adminservices.deleteUser(req, res, id);
    }

    @PostMapping("/upload/bulk/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> uploadUserData(HttpServletRequest req,
            HttpServletResponse res,
            @RequestParam("file") MultipartFile file) {
        UserInfoDTO userDetails = (UserInfoDTO) req.getAttribute("user");
        return adminservices.uploadUsersData(req, res, file, userDetails);
    }

    @PostMapping("/update/books/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateBooksByBookId(HttpServletRequest req, HttpServletResponse res,
            @PathVariable("id") String id, @RequestBody BookservicesDTO bookservicesDTO) {

        return adminservices.updateBooksByBookId(req, res, id, bookservicesDTO);
    }

    @DeleteMapping("/delete/book/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteBooksById(HttpServletRequest req, HttpServletResponse res,
            @PathVariable("id") String id) {

        return adminservices.deleteBooksByBookId(req, res, id);
    }

    @PostMapping("/assign/book/user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> assignBookToUser(HttpServletRequest req, HttpServletResponse res,
            @RequestBody StudentBookDTO studentBookDTO) {

        return adminservices.assignBookToUser(req, res, studentBookDTO);
    }

    @GetMapping("/get/user/books")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<?> fetchUserBooksByUserId(HttpServletRequest req, HttpServletResponse res,
            @RequestParam String id) {

        return adminservices.fetchUserBooksByUserId(req, res, id);
    }

    @GetMapping("/user/book/id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> fetchUserBooksByBookId(HttpServletRequest req, HttpServletResponse res,
            @RequestBody BookservicesDTO bookservicesDTO, @RequestParam int page, @RequestParam int size) {

        return adminservices.fetchUserBooksByBookId(req, res, bookservicesDTO, page, size);
    }

}
