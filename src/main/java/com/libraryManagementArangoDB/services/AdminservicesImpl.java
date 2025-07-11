package com.libraryManagementArangoDB.services;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.libraryManagementArangoDB.config.CustomResponse;
import com.libraryManagementArangoDB.dao.AdminDAO;
import com.libraryManagementArangoDB.dto.BookservicesDTO;
import com.libraryManagementArangoDB.dto.StudentBookDTO;
import com.libraryManagementArangoDB.dto.UserBookViewDTO;
import com.libraryManagementArangoDB.dto.UserInfoDTO;
import com.libraryManagementArangoDB.dto.UserServiceDTO;
import com.libraryManagementArangoDB.model.BookCollection;
import com.libraryManagementArangoDB.model.StudentBookCollection;
import com.libraryManagementArangoDB.model.UserCollection;
import com.libraryManagementArangoDB.utills.Utill;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AdminservicesImpl implements Adminservices {

    @Autowired
    AdminDAO adminDAO;

    @Autowired
    Utill utills;

    @Override
    public ResponseEntity<?> getStudentRole(HttpServletRequest req, HttpServletResponse res, int page, int size) {

        try {

            String role = "ROLE_STUDENT,";

            Pageable pageable = PageRequest.of(page, size);

            Page<UserCollection> studentDataPage = adminDAO.getStudentRole(role, pageable);

            CustomResponse<?> responseBody = new CustomResponse<>(studentDataPage, "SUCCESS",
                    HttpStatus.OK.value(),
                    req.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (Exception e) {

            CustomResponse<String> responseBody = new CustomResponse<>(e.getMessage(),
                    "BAD_REQUEST",
                    HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);

        }

    }

    @Override
    public ResponseEntity<?> uploadBooksData(HttpServletRequest req,
            HttpServletResponse res, MultipartFile file,
            UserInfoDTO userDetails) {

        try {
            if (file.isEmpty()) {

                String errorMessage = "File is Empty !";

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessage,
                        "NOT_FOUND",
                        HttpStatus.NOT_FOUND.value(), req.getRequestURI(), LocalDateTime.now());

                return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);

            }

            if (!file.getOriginalFilename().endsWith(".xls") &&
                    !file.getOriginalFilename().endsWith(".xlsx")) {
                String errorMessage = "Invalid file type! Please upload a valid Excel file.";
                CustomResponse<String> responseBody = new CustomResponse<>(errorMessage,
                        "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            List<BookCollection> books = new ArrayList<>();
            try {
                Workbook workbook = new XSSFWorkbook(file.getInputStream());
                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(0);

                validateBookHeaders(headerRow);

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (rowEmpty(row)) {
                        continue;
                    }
                    BookCollection book = validateAndParseBookRow(row, userDetails);
                    books.add(book);
                }
            } catch (Exception e) {
                CustomResponse<String> responseBody = new CustomResponse<>(e.getMessage(),
                        "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            List<BookCollection> booksList = adminDAO.uploadBooks(books);

            CustomResponse<?> responseBody = new CustomResponse<>(booksList, "SUCCESS",
                    HttpStatus.OK.value(),
                    req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        } catch (Exception e) {
            CustomResponse<String> responseBody = new CustomResponse<>(e.getMessage(),
                    "BAD_REQUEST",
                    HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
    }

    private void validateBookHeaders(Row headerRow) {
        List<String> requiredHeaders = List.of("bookName", "author", "description", "noOfSets");
        for (int i = 0; i < requiredHeaders.size(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null ||
                    !requiredHeaders.get(i).equalsIgnoreCase(cell.getStringCellValue().trim())) {
                throw new IllegalArgumentException("Invalid column name: " +
                        requiredHeaders.get(i));
            }
        }
    }

    private boolean rowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String cellValue = cell.toString().trim();
                if (!cellValue.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private BookCollection validateAndParseBookRow(Row row, UserInfoDTO userDetails) {
        BookCollection book = new BookCollection();

        // Read "bookName" (column 0)
        Cell bookNameCell = row.getCell(0);
        if (bookNameCell != null && bookNameCell.getCellType() == CellType.STRING) {
            String bookName = bookNameCell.getStringCellValue().trim();
            if (!bookName.isEmpty()) {
                book.setBookName(bookName);
            } else {
                throw new IllegalArgumentException("Invalid bookName format at row " + (row.getRowNum() + 1));
            }
        } else if (bookNameCell != null && bookNameCell.getCellType() == CellType.NUMERIC) {
            book.setBookName(String.valueOf(bookNameCell.getNumericCellValue()).trim());
        } else {
            throw new IllegalArgumentException("Please enter the field bookName at row "
                    + (row.getRowNum() + 1));
        }

        // Read "author" (column 1)
        Cell authorCell = row.getCell(1);
        if (authorCell != null && authorCell.getCellType() == CellType.STRING) {
            String authorName = authorCell.getStringCellValue().trim();
            if (!authorName.isEmpty()) {
                book.setAuthor(authorName);
            } else {
                throw new IllegalArgumentException("Invalid authorName format at row " + (row.getRowNum() + 1));
            }
        } else if (authorCell != null && authorCell.getCellType() == CellType.NUMERIC) {
            book.setAuthor(String.valueOf(authorCell.getNumericCellValue()).trim());
        } else {
            throw new IllegalArgumentException("Please enter the field authorName at row "
                    + (row.getRowNum() + 1));
        }

        // Read "description" (column 2)
        Cell descriptionCell = row.getCell(2);
        if (descriptionCell != null && descriptionCell.getCellType() == CellType.STRING) {
            String description = descriptionCell.getStringCellValue().trim();
            if (!description.isEmpty()) {
                book.setDescription(description);
            } else {
                throw new IllegalArgumentException("Invalid description format at row " + (row.getRowNum() + 1));
            }
        } else if (descriptionCell != null && descriptionCell.getCellType() == CellType.NUMERIC) {
            book.setDescription(String.valueOf(descriptionCell.getNumericCellValue()).trim());
        } else {
            throw new IllegalArgumentException("Please enter the field description at row "
                    + (row.getRowNum() + 1));
        }

        Cell noOFSetsCell = row.getCell(3);
        if (noOFSetsCell != null && noOFSetsCell.getCellType() == CellType.NUMERIC) {
            book.setNoOfSets((int) noOFSetsCell.getNumericCellValue());
        } else {
            throw new IllegalArgumentException("Please enter the field no of sets at row" + (row.getRowNum() + 1));
        }

        book.setCreatedAt(LocalDateTime.now());
        book.setCreatedBy(userDetails.getUuid());

        return book;
    }

    @Override
    public ResponseEntity<?> updateUserById(HttpServletRequest req, HttpServletResponse res, String id,
            UserServiceDTO userservicesDTO, UserInfoDTO userDetails) {

        try {

            // if (id.isBlank() || id.isEmpty()) {

            // String errorMessages = "Id is required!";

            // CustomResponse<String> responseBody = new CustomResponse<>(errorMessages,
            // "BAD_REQUEST",
            // HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());

            // return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            // }

            UserServiceDTO updateUser = adminDAO.updateUserInfo(id, userservicesDTO,
                    userDetails);

            System.out.println("UPDATE USER " + " " + updateUser.getId());

            CustomResponse<?> responseBody = new CustomResponse<>(updateUser, "UPDATED", HttpStatus.OK.value(),
                    req.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        } catch (Exception e) {

            CustomResponse<String> responseBody = new CustomResponse<>(e.getMessage(),
                    "BAD_REQUEST",
                    HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);

        }

    }

    @Override
    public ResponseEntity<?> deleteUser(HttpServletRequest req, HttpServletResponse res, String id) {

        try {

            if (id.isBlank()) {

                String errorMessages = "Id is required!";

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessages, "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());

                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            Optional<UserCollection> deleteUser = adminDAO.deleteUserInfo(id);

            if (deleteUser.isEmpty()) {

                String errorMessage = "User not found with ID: " + id;

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessage, "NOT_FOUND",
                        HttpStatus.NOT_FOUND.value(), req.getRequestURI(), LocalDateTime.now());

                return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
            }

            CustomResponse<?> responseBody = new CustomResponse<>("User deleted successfully", "DELETED",
                    HttpStatus.OK.value(),
                    req.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        } catch (Exception e) {

            CustomResponse<String> responseBody = new CustomResponse<>(e.getMessage(),
                    "BAD_REQUEST",
                    HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);

        }

    }

    @Override
    public ResponseEntity<?> uploadUsersData(HttpServletRequest req,
            HttpServletResponse res, MultipartFile file, UserInfoDTO userDetails) {

        try {
            if (file.isEmpty()) {

                String errorMessage = "File is Empty !";

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessage,
                        "NOT_FOUND",
                        HttpStatus.NOT_FOUND.value(), req.getRequestURI(), LocalDateTime.now());

                return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);

            }

            if (!file.getOriginalFilename().endsWith(".xls")
                    && !file.getOriginalFilename().endsWith(".xlsx")) {
                String errorMessage = "Invalid file type! Please upload a valid Excel file.";
                CustomResponse<String> responseBody = new CustomResponse<>(errorMessage,
                        "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            List<UserCollection> users = new ArrayList<>();
            List<String> emails = new ArrayList<>();

            try {
                Workbook workbook = new XSSFWorkbook(file.getInputStream());
                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(0);

                validateUserHeaders(headerRow);

                System.out.println(sheet.getLastRowNum());

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    // Skip rows where any cell has an empty string
                    if (isRowEmpty(row)) {
                        continue;
                    }
                    UserCollection user = validateParseRow(row, userDetails);
                    users.add(user);
                }

                for (int emailIndex = 1; emailIndex <= sheet.getLastRowNum(); emailIndex++) {
                    Row row = sheet.getRow(emailIndex);
                    emails.add(row.getCell(1).toString());
                }

                List<UserCollection> existingUsers = adminDAO.getExisitingUsers(emails);

                if (existingUsers.toArray().length > 0) {

                    List<String> emailList = existingUsers.stream()
                            .map(UserCollection::getEmail) // Extracts only the email field
                            .toList();

                    String emailString = String.join(", ", emailList);

                    String message = "User email already exists: " + emailString;

                    CustomResponse<String> responseBody = new CustomResponse<>(message,
                            "NOT_FOUND",
                            HttpStatus.NOT_FOUND.value(), req.getRequestURI(), LocalDateTime.now());

                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }

            } catch (Exception e) {

                CustomResponse<String> responseBody = new CustomResponse<>(e.getMessage(),
                        "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);

            }

            List<UserCollection> userList = adminDAO.uploadUserInfo(users);

            CustomResponse<?> responseBody = new CustomResponse<>(userList, "SUCCESS",
                    HttpStatus.OK.value(),
                    req.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        } catch (Exception e) {
            CustomResponse<String> responseBody = new CustomResponse<>(e.getMessage(),
                    "BAD_REQUEST",
                    HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
    }

    private void validateUserHeaders(Row headerRow) {
        List<String> requiredHeaders = List.of("username", "email", "phone",
                "country", "state", "dob");
        for (int i = 0; i < requiredHeaders.size(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null
                    || !requiredHeaders.get(i).equalsIgnoreCase(cell.getStringCellValue().trim())) {
                throw new IllegalArgumentException("Invalid column name: "
                        + requiredHeaders.get(i));
            }
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String cellValue = cell.toString().trim();
                if (!cellValue.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private UserCollection validateParseRow(Row row, UserInfoDTO userDetails) {
        UserCollection user = new UserCollection();

        // Assuming the expected columns are in specific positions (adjust as needed)
        // For example, column 0 is "username", column 1 is "email", etc.
        user.setUuid(utills.generateString(36));
        // Read "username" (column 0)
        Cell usernameCell = row.getCell(0);
        if (usernameCell != null && usernameCell.getCellType() == CellType.STRING) {
            user.setUserName(usernameCell.getStringCellValue().trim());
        } else {
            // Handle error if the value is not a string or is empty
            throw new IllegalArgumentException("Please enter the field username at "
                    + (row.getRowNum() + 1));
        }

        // Read "email" (column 1)
        Cell emailCell = row.getCell(1);
        if (emailCell != null && emailCell.getCellType() == CellType.STRING) {
            user.setEmail(emailCell.getStringCellValue().trim());
        } else {
            throw new IllegalArgumentException("Please enter the field email at "
                    + (row.getRowNum() + 1));
        }

        // Read "phone" (column 2)
        Cell phoneCell = row.getCell(2);
        if (phoneCell != null) {
            if (phoneCell.getCellType() == CellType.NUMERIC) {
                // Convert numeric phone number to string
                String phone = String.valueOf((long) phoneCell.getNumericCellValue());

                // Validate phone number format
                if (phone.matches("\\d{10}")) {
                    user.setPhone(phone);
                } else {
                    throw new IllegalArgumentException(
                            "Invalid phone number. Please enter a valid 10-digit phone number at"
                                    + (row.getRowNum() + 1));
                }
            } else {
                throw new IllegalArgumentException(
                        "Invalid phone cell type. Phone number must be a string or numeric at"
                                + (row.getRowNum() + 1));
            }
        } else {
            throw new IllegalArgumentException(
                    "Phone field is missing. Please enter the phone number at "
                            + (row.getRowNum() + 1));
        }

        // Read "country" (column 3)
        Cell countryCell = row.getCell(3);
        if (countryCell != null && countryCell.getCellType() == CellType.STRING) {
            user.setCountry(countryCell.getStringCellValue().trim());
        } else {
            throw new IllegalArgumentException("Please enter the field country at"
                    + (row.getRowNum() + 1));
        }

        // Read "state" (column 4)
        Cell stateCell = row.getCell(4);
        if (stateCell != null && stateCell.getCellType() == CellType.STRING) {
            user.setState(stateCell.getStringCellValue().trim());
        } else {
            throw new IllegalArgumentException("Please enter the field state at"
                    + (row.getRowNum() + 1));
        }
        // Read "dob" (column 5), assuming it's a date (you can adjust for other
        // formats)
        Cell dobCell = row.getCell(5);
        if (dobCell != null) {
            if (dobCell.getCellType() == CellType.NUMERIC) {
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(dobCell)) {
                    // Convert date to string in desired format
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    user.setDob(sdf.format(dobCell.getDateCellValue())); // Convert date to string
                } else {
                    throw new IllegalArgumentException("Please enter the Valid dob at"
                            + (row.getRowNum() + 1));
                }
            } else {
                throw new IllegalArgumentException("DOB number should be in "
                        + dobCell.getCellType() + (row.getRowNum() + 1));
            }
        } else {
            throw new IllegalArgumentException("Please enter the field dob at "
                    + (row.getRowNum() + 1));
        }

        user.setRole("ROLE_STUDENT,");
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy(userDetails.getUuid());
        SecureRandom secureRandom = new SecureRandom();
        String sixDigitNumber = String.valueOf(100000 + secureRandom.nextInt(900000)); // Generates a number between
        // 100000 and 999999
        System.out.println("6-Digit Secure Random Number: " + sixDigitNumber);
        user.setRollNo(sixDigitNumber);

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(utills.generateRandomPassword()); // Set a random password if it's not provided
        }
        System.out.println(user.getUserName());
        return user;
    }

    @Override
    public ResponseEntity<?> updateBooksByBookId(HttpServletRequest req, HttpServletResponse res, String id,
            BookservicesDTO bookservicesDTO) {

        try {

            if (id.isEmpty()) {

                String errorMessages = "Id is required!";

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessages, "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());

                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            BookservicesDTO updateBooks = adminDAO.updateBooksInfo(id, bookservicesDTO);

            System.out.println("UPDATE BOOKS" + " " + updateBooks);

            CustomResponse<?> responseBody = new CustomResponse<>(updateBooks, "SUCCESS",
                    HttpStatus.OK.value(),
                    req.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        } catch (Exception e) {

            CustomResponse<String> responseBody = new CustomResponse<>(e.getMessage(),
                    "BAD_REQUEST",
                    HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);

        }
    }

    @Override
    public ResponseEntity<?> deleteBooksByBookId(HttpServletRequest req, HttpServletResponse res, String id) {

        try {

            if (id.isBlank()) {

                String errorMessages = "Id is required!";

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessages, "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());

                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            Optional<BookCollection> deleteBook = adminDAO.deleteBookInfo(id);

            if (deleteBook.isEmpty()) {

                String errorMessage = "Book not found with ID: " + id;

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessage, "NOT_FOUND",
                        HttpStatus.NOT_FOUND.value(), req.getRequestURI(), LocalDateTime.now());

                return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
            }

            CustomResponse<?> responseBody = new CustomResponse<>("Book deleted successfully", "DELETED",
                    HttpStatus.OK.value(),
                    req.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        } catch (Exception e) {

            String stackTrace = utills.getStackTraceAsString(e);

            CustomResponse<String> responseBody = new CustomResponse<>(stackTrace,
                    "BAD_REQUEST",
                    HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);

        }
    }

    @Override
    public ResponseEntity<?> assignBookToUser(HttpServletRequest req, HttpServletResponse res,
            StudentBookDTO studentBookDTO) {
        try {

            Optional<BookCollection> getBookDetails = adminDAO.getBookById(studentBookDTO.getBookId());

            Optional<UserCollection> getUserDetails = adminDAO.getUserByRollNumber(studentBookDTO.getRollNumber());

            if (getBookDetails.get().getId().isEmpty() || getUserDetails.get().getId().isEmpty()) {
                String errorMessage = "Book ID or User ID is required!";

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessage, "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());

                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            if (getBookDetails.get().getNoOfSets() <= 0) {
                String errorMessage = "No sets available for the book";

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessage, "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());

                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            Optional<StudentBookCollection> checkBookAssigned = adminDAO.checkBookAssigned(getBookDetails.get().getId(),
                    getUserDetails.get().getId());

            if (checkBookAssigned.isEmpty()) {
                BookCollection bookEntity = getBookDetails.get();
                bookEntity.setNoOfSets(getBookDetails.get().getNoOfSets() - 1);
                bookEntity.setUpdatedAt(LocalDateTime.now());
                bookEntity.setUpdatedBy(getUserDetails.get().getUuid());
                adminDAO.updateBookDetails(bookEntity);

                StudentBookCollection studentBook = new StudentBookCollection();
                // studentBook.setBook(getBookDetails.get());
                // studentBook.setUser(getUserDetails.get());
                studentBook.setStatus("Pending");
                studentBook.setSubmissionDate(LocalDateTime.now().plusDays(10));
                studentBook.setCreatedAt(LocalDateTime.now());
                studentBook.setCreatedBy(getUserDetails.get().getUuid());
                StudentBookCollection createStudentBook = adminDAO.createStudentBook(studentBook);

                CustomResponse<?> responseBody = new CustomResponse<>(createStudentBook, "SUCCESS",
                        HttpStatus.OK.value(),
                        req.getRequestURI(), LocalDateTime.now());

                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else if (checkBookAssigned.get().getStatus().equalsIgnoreCase("submited")) {
                BookCollection bookEntity = getBookDetails.get();
                bookEntity.setNoOfSets(getBookDetails.get().getNoOfSets() - 1);
                bookEntity.setUpdatedAt(LocalDateTime.now());
                bookEntity.setUpdatedBy(getUserDetails.get().getUuid());
                adminDAO.updateBookDetails(bookEntity);

                StudentBookCollection studentBook = checkBookAssigned.get();
                studentBook.setStatus("Pending");
                studentBook.setSubmissionDate(LocalDateTime.now().plusDays(10));
                studentBook.setUpdatedAt(LocalDateTime.now());
                studentBook.setUpdatedBy(getUserDetails.get().getUuid());

                StudentBookCollection updateStudentBook = adminDAO.createStudentBook(studentBook);

                CustomResponse<?> responseBody = new CustomResponse<>(updateStudentBook, "SUCCESS",
                        HttpStatus.OK.value(),
                        req.getRequestURI(), LocalDateTime.now());

                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {

                BookCollection bookEntity = getBookDetails.get();
                bookEntity.setNoOfSets(getBookDetails.get().getNoOfSets() + 1);
                bookEntity.setUpdatedAt(LocalDateTime.now());
                bookEntity.setUpdatedBy(getUserDetails.get().getUuid());
                adminDAO.updateBookDetails(bookEntity);

                StudentBookCollection studentBook = checkBookAssigned.get();
                studentBook.setStatus("Submited");
                studentBook.setSubmissionDate(LocalDateTime.now());
                studentBook.setUpdatedAt(LocalDateTime.now());
                studentBook.setUpdatedBy(getUserDetails.get().getUuid());

                StudentBookCollection updateStudentBook = adminDAO.createStudentBook(studentBook);

                CustomResponse<?> responseBody = new CustomResponse<>(updateStudentBook, "SUCCESS",
                        HttpStatus.OK.value(),
                        req.getRequestURI(), LocalDateTime.now());

                return new ResponseEntity<>(responseBody, HttpStatus.OK);

            }
        } catch (Exception e) {

            CustomResponse<String> responseBody = new CustomResponse<>(e.getMessage(),
                    "BAD_REQUEST",
                    HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> fetchUserBooksByUserId(HttpServletRequest req, HttpServletResponse res,
            String id) {
        try {

            if (id.isEmpty()) {

                String errorMessages = "Id is required!";

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessages, "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());

                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            List<UserBookViewDTO> getUserBooks = adminDAO.getUserBooksById(id);

            CustomResponse<?> responseBody = new CustomResponse<>(getUserBooks, "SUCCESS",
                    HttpStatus.OK.value(),
                    req.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        } catch (Exception e) {

            CustomResponse<String> responseBody = new CustomResponse<>(e.getMessage(),
                    "BAD_REQUEST",
                    HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);

        }
    }

    @Override
    public ResponseEntity<?> fetchUserBooksByBookId(HttpServletRequest req, HttpServletResponse res,
            BookservicesDTO bookservicesDTO, int page, int size) {
        try {

            String id = bookservicesDTO.getId() != null ? bookservicesDTO.getId() : null;

            if (id == null) {

                String errorMessages = "Id is required!";

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessages, "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());

                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            Map<String, Object> finalUserBooksList = adminDAO.getUserBooksInfoByBookId(id, page, size);

            CustomResponse<?> responseBody = new CustomResponse<>(finalUserBooksList, "SUCCESS",
                    HttpStatus.OK.value(),
                    req.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        } catch (Exception e) {

            CustomResponse<String> responseBody = new CustomResponse<>(e.getMessage(),
                    "BAD_REQUEST",
                    HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);

        }
    }

}
