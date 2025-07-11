package com.libraryManagementArangoDB.dao;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;

import com.libraryManagementArangoDB.dto.BookservicesDTO;
import com.libraryManagementArangoDB.dto.UserBookViewDTO;
import com.libraryManagementArangoDB.dto.UserInfoDTO;
import com.libraryManagementArangoDB.dto.UserServiceDTO;
import com.libraryManagementArangoDB.model.BookCollection;
import com.libraryManagementArangoDB.model.StudentBookCollection;
import com.libraryManagementArangoDB.model.UserCollection;

public interface AdminDAO {

    Page<UserCollection> getStudentRole(String role, Pageable pageable);

    UserServiceDTO updateUserInfo(String id, UserServiceDTO userservicesDTO, UserInfoDTO userDetails);

    Optional<UserCollection> deleteUserInfo(String id);

    List<UserCollection> getExisitingUsers(List<String> emails);

    List<UserCollection> uploadUserInfo(List<UserCollection> users);

    List<BookCollection> uploadBooks(List<BookCollection> books);

    BookservicesDTO updateBooksInfo(String id, BookservicesDTO bookservicesDTO);

    Optional<BookCollection> deleteBookInfo(String id);

    Optional<BookCollection> getBookById(String bookId);

    Optional<UserCollection> getUserByRollNumber(String rollNumber);

    BookCollection updateBookDetails(BookCollection bookEntity);

    StudentBookCollection createStudentBook(StudentBookCollection studentBook);

    public Optional<StudentBookCollection> checkBookAssigned(String bookId, String userId);

    List<UserBookViewDTO> getUserBooksById(String id);

    Map<String, Object> getUserBooksInfoByBookId(String id, int page, int size);

}
