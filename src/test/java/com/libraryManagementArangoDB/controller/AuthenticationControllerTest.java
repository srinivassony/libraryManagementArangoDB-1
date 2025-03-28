package com.libraryManagementArangoDB.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryManagementArangoDB.config.CustomResponse;
import com.libraryManagementArangoDB.dto.UserServiceDTO;
import com.libraryManagementArangoDB.services.RegistrationService;
import com.libraryManagementArangoDB.utills.JwtTokenProvider;
import com.libraryManagementArangoDB.utills.UserInfo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private WebApplicationContext webApplicationContext;

        @MockBean
        RegistrationService registrationService;

        @MockBean
        private HttpServletRequest request; // Mock HttpServletRequest

        @MockBean
        private HttpServletResponse response; // Mock HttpServletResponse

        @MockBean
        private JwtTokenProvider jwtTokenProvider;

        @MockBean
        private UserInfo userInfo;

        @BeforeEach
        public void setUp() {
                mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }

        @Test
        public void SignUpTest_SuccessResult() throws Exception {

                UserServiceDTO userServiceDTO = new UserServiceDTO();
                userServiceDTO.setUserName("test");
                userServiceDTO.setPassword("test");
                userServiceDTO.setEmail("srinivas@gmail.com");

                // Create a CustomResponse wrapping UserCollection
                CustomResponse<UserServiceDTO> customResponse = new CustomResponse<>(userServiceDTO, "CREATED",
                                HttpStatus.OK.value(),
                                "/add/user", LocalDateTime.now());

                Mockito.when(registrationService.createUserInfo(Mockito.any(), Mockito.any(), Mockito.any()))
                                .thenAnswer(invocation -> new ResponseEntity<>(customResponse, HttpStatus.OK));

                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/add/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(new ObjectMapper().writeValueAsString(userServiceDTO)))
                                .andDo(MockMvcResultHandlers.print()) // Prints actual response
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.responseBody.data.userName")
                                                .value("test"))
                                .andReturn();

                String responseContent = result.getResponse().getContentAsString();
                System.out.println("Actual Response: " + responseContent);
        }

        @Test
        public void SignUpTest_ValidationErrorResult() throws Exception {

                UserServiceDTO userServiceDTO = new UserServiceDTO();
                userServiceDTO.setPassword("test");
                userServiceDTO.setEmail("srinivas@gmail.com");

                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/add/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(new ObjectMapper().writeValueAsString(userServiceDTO)))
                                .andDo(MockMvcResultHandlers.print()) // Prints actual response
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.responseBody.message")
                                                .value(org.hamcrest.Matchers
                                                                .containsString("UserName must not be null.")))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.responseBody.message")
                                                .value(org.hamcrest.Matchers.containsString("UserName is required!;")))
                                .andReturn();

                String responseContent = result.getResponse().getContentAsString();
                System.out.println("Actual Response:2 " + responseContent);
        }
}
