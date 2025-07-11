package com.libraryManagementArangoDB.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.libraryManagementArangoDB.dto.BookservicesDTO;
import com.libraryManagementArangoDB.dto.StudentBookDTO;
import com.libraryManagementArangoDB.dto.UserInfoDTO;
import com.libraryManagementArangoDB.dto.UserServiceDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Adminservices {

        ResponseEntity<?> getStudentRole(HttpServletRequest req, HttpServletResponse res, int page, int size);

        ResponseEntity<?> updateUserById(HttpServletRequest req, HttpServletResponse res, String id,
                        UserServiceDTO userservicesDTO, UserInfoDTO userDetails);

        ResponseEntity<?> deleteUser(HttpServletRequest req, HttpServletResponse res, String id);

        ResponseEntity<?> uploadUsersData(HttpServletRequest req, HttpServletResponse res, MultipartFile file,
                        UserInfoDTO userDetails);

        ResponseEntity<?> uploadBooksData(HttpServletRequest req, HttpServletResponse res, MultipartFile file,
                        UserInfoDTO userDetails);

        ResponseEntity<?> updateBooksByBookId(HttpServletRequest req, HttpServletResponse res, String id,
                        BookservicesDTO bookservicesDTO);

        ResponseEntity<?> deleteBooksByBookId(HttpServletRequest req, HttpServletResponse res, String id);

        ResponseEntity<?> assignBookToUser(HttpServletRequest req, HttpServletResponse res,
                        StudentBookDTO studentBookDTO);

        ResponseEntity<?> fetchUserBooksByUserId(HttpServletRequest req, HttpServletResponse res,
                        String id);

        ResponseEntity<?> fetchUserBooksByBookId(HttpServletRequest req, HttpServletResponse res,
                        BookservicesDTO bookservicesDTO, int page, int size);
}
