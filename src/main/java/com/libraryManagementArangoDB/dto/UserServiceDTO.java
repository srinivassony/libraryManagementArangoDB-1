package com.libraryManagementArangoDB.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceDTO {

    private String id; // ArangoDB uses String ID

    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    @NotBlank(message = "UserName is required!")
    private String userName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required!")
    private String email;

    @NotBlank(message = "Password is required!")
    private String password;

    private String role;

    private String uuid;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private String dob;

    private String rollNo;

    private String country;

    private String state;

    private String gender;

    private LocalDateTime createdAt;

    private String createdBy;

    private Boolean isAdmin;

    private LocalDateTime updatedAt;

    private String updatedBy;
}
