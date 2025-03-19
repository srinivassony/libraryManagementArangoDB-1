package com.libraryManagementArangoDB.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.libraryManagementArangoDB.config.CustomResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CustomErrorController {

    @RequestMapping("/error")
    public ResponseEntity<?> handleError(HttpServletRequest request, HttpServletResponse res) {
        // Get the error status code
        Integer statusCode = res.getStatus();

        System.out.println("statusCode" + " " + statusCode);

        if (statusCode == 404) {

            String errorMessages = "The resource you are looking for could not be found.";

            CustomResponse<String> responseBody = new CustomResponse<>(errorMessages, "NOT_FOUND",
                    HttpStatus.NOT_FOUND.value(), request.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);

        } else if (statusCode == 500) {

            String errorMessages = "An unexpected error occurred. Please try again later.";

            CustomResponse<String> responseBody = new CustomResponse<>(errorMessages, "INTERNAL_SERVER_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
        } else if (statusCode == 403) {

            String errorMessages = "You do not have permission to access this resource.";

            CustomResponse<String> responseBody = new CustomResponse<>(errorMessages, "FORBIDDEN_ERROR",
                    HttpStatus.FORBIDDEN.value(), request.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        } else if (statusCode == 405) {

            String errorMessages = "The HTTP method used is not allowed for this request. Please check the request method and try again.";

            CustomResponse<String> responseBody = new CustomResponse<>(errorMessages, "METHOD_NOT_ALLOWED",
                    HttpStatus.METHOD_NOT_ALLOWED.value(), request.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        } else {
            System.out.println("herrrrrrr");

            String errorMessages = "Something went wrong!";

            CustomResponse<String> responseBody = new CustomResponse<>(errorMessages, "BAD_REQUEST",
                    statusCode, request.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
    }
}
