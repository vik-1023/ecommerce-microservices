package com.ecommerce.auth.service.impl;

import com.ecommerce.auth.dto.request.RegisterRequest;
import com.ecommerce.auth.dto.response.RegisterResponse;

import com.ecommerce.auth.exception.EmailAlreadyExistException;
import com.ecommerce.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.Mockito.never;
import java.util.Optional;


import com.ecommerce.auth.entity.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testRegisterUser() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("vikram@gmail.com");
        request.setPassword("Password@123");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("hashed-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RegisterResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("vikram@gmail.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());
        assertEquals("ACTIVE", response.getStatus());

        verify(passwordEncoder).encode("Password@123");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("hashed-password", savedUser.getPassword());
    }


    @Test
    void testRegisterUser_EmailAlreadyExists() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("vikram@gmail.com");
        request.setPassword("Password@123");

        User existingUser = User.builder()
                .id(1L)
                .email("vikram@gmail.com")
                .build();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(existingUser));

        assertThrows(
                EmailAlreadyExistException.class,
                () -> authService.register(request)
        );
        verify(userRepository, never()).save(any(User.class));
    }
}

