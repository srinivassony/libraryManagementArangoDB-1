package com.libraryManagementArangoDB.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arangodb.ArangoCollection;
import com.arangodb.entity.BaseDocument;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.ArangoOperations;
import com.libraryManagementArangoDB.dto.BookservicesDTO;
import com.libraryManagementArangoDB.dto.UserBookViewDTO;
import com.libraryManagementArangoDB.dto.UserInfoDTO;
import com.libraryManagementArangoDB.dto.UserServiceDTO;
import com.libraryManagementArangoDB.model.BookCollection;
import com.libraryManagementArangoDB.model.StudentBookCollection;
import com.libraryManagementArangoDB.model.UserCollection;
import com.libraryManagementArangoDB.repository.BookRepo;
import com.libraryManagementArangoDB.repository.StudentBookRepo;
import com.libraryManagementArangoDB.repository.UserRepo;
import com.libraryManagementArangoDB.utills.UtillDTO;

@Service
public class AdminDAOImpl implements AdminDAO {

    @Autowired
    UserRepo userRepo;

    @Autowired
    BookRepo bookRepo;

    @Autowired
    UtillDTO utillDTO;

    @Autowired
    StudentBookRepo studentBookRepo;

    private static final int BATCH_SIZE = 1000; // Define batch size

    @Override
    public Page<UserCollection> getStudentRole(String role, Pageable pageable) {
        // TODO Auto-generated method stub

        Page<UserCollection> user = userRepo.findByRole(role, pageable);
        if (!user.isEmpty()) {
            return user;
        } else {
            return Page.empty();
        }
    }

    @Override
    public List<BookCollection> uploadBooks(List<BookCollection> books) {

        if (books == null || books.isEmpty()) {
            return new ArrayList<>();
        }

        List<BookCollection> savedBooks = new ArrayList<>();

        // Split the list into batches
        for (int i = 0; i < books.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, books.size());
            List<BookCollection> batch = books.subList(i, end);

            try {
                bookRepo.saveAll(batch); // Bulk insert!
                savedBooks.addAll(batch);
            } catch (Exception e) {
                System.err.println("Error inserting batch: " + e.getMessage());
            }
        }

        return savedBooks;
    }

    @Override
    public UserServiceDTO updateUserInfo(String id, UserServiceDTO userservicesDTO, UserInfoDTO userDetails) {
        System.out.println("Converted ObjectId: " + id);
        return userRepo.findById(id)
                .map(entity -> {
                    // Update other fields as needed

                    System.out.println("✅ User found in DB: " + entity);

                    if (userservicesDTO.getUserName() != null) {
                        entity.setUserName(userservicesDTO.getUserName());
                    }

                    if (userservicesDTO.getEmail() != null) {
                        entity.setEmail(userservicesDTO.getEmail());
                    }

                    if (userservicesDTO.getPhone() != null) {
                        entity.setPhone(userservicesDTO.getPhone());
                    }

                    if (userservicesDTO.getCountry() != null) {
                        entity.setCountry(userservicesDTO.getCountry());
                    }

                    if (userservicesDTO.getState() != null) {
                        entity.setState(userservicesDTO.getState());
                    }

                    if (userservicesDTO.getDob() != null) {
                        entity.setDob(userservicesDTO.getDob());
                    }

                    if (userservicesDTO.getGender() != null) {
                        entity.setGender(userservicesDTO.getGender());
                    }

                    entity.setUpdatedAt(LocalDateTime.now());
                    entity.setUpdatedBy(userDetails.getUuid());

                    UserCollection userCollection = userRepo.save(entity);
                    return utillDTO.convertToUserDTO(userCollection);

                }).orElseThrow(() -> new UsernameNotFoundException("User not found with id "
                        + id));
    }

    @Override
    public Optional<UserCollection> deleteUserInfo(String id) {
        Optional<UserCollection> user = userRepo.findById(id);
        if (user.isPresent()) {
            // Delete the user entity
            userRepo.deleteById(id);
            return user;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<UserCollection> getExisitingUsers(List<String> emails) {

        List<UserCollection> users = userRepo.findUsersByEmail(emails);

        return users;

    }

    @Transactional
    @Override
    public List<UserCollection> uploadUserInfo(List<UserCollection> users) {
        if (users == null || users.isEmpty()) {
            return new ArrayList<>();
        }

        List<UserCollection> savedUsers = new ArrayList<>();

        // Split the list into batches
        for (int i = 0; i < users.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, users.size());
            List<UserCollection> batch = users.subList(i, end);

            try {
                userRepo.saveAll(batch); // Bulk insert!
                savedUsers.addAll(batch);
            } catch (Exception e) {
                System.err.println("Error inserting batch: " + e.getMessage());
            }
        }

        return savedUsers;
    }

    @Override
    public BookservicesDTO updateBooksInfo(String id, BookservicesDTO bookservicesDTO) {
        // TODO Auto-generated method stub

        return bookRepo.findById(id)
                .map(entity -> {
                    // Update other fields as needed
                    if (bookservicesDTO.getBookName() != null) {
                        entity.setBookName(bookservicesDTO.getBookName());
                    }

                    if (bookservicesDTO.getAuthor() != null) {
                        entity.setAuthor(bookservicesDTO.getAuthor());
                    }

                    if (bookservicesDTO.getDescription() != null) {
                        entity.setDescription(bookservicesDTO.getDescription());
                    }

                    if (bookservicesDTO.getNoOfSets() != null) {
                        entity.setNoOfSets(bookservicesDTO.getNoOfSets());
                    }

                    entity.setUpdatedAt(LocalDateTime.now());
                    // entity.setUpdatedBy(userDetails.getUuid());

                    BookCollection bookEntity = bookRepo.save(entity);
                    return utillDTO.convertToBookDTO(bookEntity);

                }).orElseThrow(() -> new UsernameNotFoundException("User not found with id "
                        + id));
    }

    @Override
    public Optional<BookCollection> deleteBookInfo(String id) {
        // TODO Auto-generated method stub

        Optional<BookCollection> deleteBook = bookRepo.findById(id);
        if (deleteBook.isPresent()) {
            bookRepo.deleteById(id);
            return deleteBook;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<BookCollection> getBookById(String bookId) {
        return bookRepo.findById(bookId);
    }

    @Override
    public Optional<UserCollection> getUserByRollNumber(String rollnumber) {
        return userRepo.findByRollNo(rollnumber);
    }

    @Override
    public BookCollection updateBookDetails(BookCollection bookEntity) {
        return bookRepo.save(bookEntity);
    }

    @Override
    public StudentBookCollection createStudentBook(StudentBookCollection studentBook) {
        return studentBookRepo.save(studentBook);
    }

    @Override
    public Optional<StudentBookCollection> checkBookAssigned(String bookId, String userId) {
        // return studentBookRepo.findByBookIdAndUserId(bookId, userId);

        return null;
    }

    @Override
    public List<UserBookViewDTO> getUserBooksById(String id) {
        // TODO Auto-generated method stub

        // return userRepo.findUserBooksById(id);

        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getUserBooksInfoByBookId(String id, int page, int size) {
        List<UserBookViewDTO> allResults = new ArrayList<>(); // Fetch all records

        int totalItems = allResults.size(); // Get total count

        // Apply pagination manually
        int start = page * size;
        int end = Math.min(start + size, totalItems);

        List<UserBookViewDTO> paginatedList = allResults.subList(start, end);

        // Prepare response
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("users", paginatedList);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", (int) Math.ceil((double) totalItems / size));

        return response;
    }

}
