package com.ecommerce.auth.controller;

import com.ecommerce.auth.dto.request.RegisterRequest;
import com.ecommerce.auth.dto.response.RegisterResponse;
import com.ecommerce.auth.exception.EmailAlreadyExistException;
import com.ecommerce.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    @MockitoBean
    private AuthService authService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerUser_WhenFieldsAreEmpty_shouldReturnBadRequest() throws Exception {
        String json = """
                {
                "email":"",
                "password":""
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors.email")
                        .value("Email is required"))
                .andExpect(jsonPath("$.errors.password")
                        .value("Password is required"));

    }

    @Test
    void registerUser_WhenEmailIsInvalid_ShouldReturnBadRequest() throws Exception {
        String json = """
                {
                    "email": "vikram",
                    "password": "Password@123"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors.email")
                        .value("invalid email format"));
    }

    @Test
    void registerUser_WhenPasswordIsTooShort_ShouldReturnBadRequest() throws Exception {
        String json = """
                {
                    "email": "vikram@gmail.com",
                    "password": "123"
                }
                """;
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors.password")
                        .value("password must be at least 8 character"));
    }

    @Test
    void registerUser_WhenRequestIsValid_ShouldReturnSuccess() throws Exception {
        String json = """
                {
                    "email": "vikram@gmail.com",
                    "password": "Password@123"
                }
                """;
        RegisterResponse response = RegisterResponse.builder()
                .id(1L)
                .email("vikram@gmail.com")
                .role("CUSTOMER")
                .status("ACTIVE")
                .build();
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("vikram@gmail.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void registerUser_WhenEmailAlreadyExists_ShouldReturnConflict() throws Exception {
        String json = """
        {
            "email": "vikram@gmail.com",
            "password": "Password@123"
        }
        """;

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new EmailAlreadyExistException("User already registered"));
        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("User already registered"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }
}