package com.libraryManagementArangoDB.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBookViewDTO {
    private String id;

    private String user_name;

    private String EMAIL;

    private String author;

    private String book_name;

    private String description;

    private Integer no_of_sets;

    private LocalDateTime submission_date;

    private String status;

    private String bookId;

}
