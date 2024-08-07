package com.example.guitarzone;

import com.example.guitarzone.model.dtos.UserAccountInfoDTO;
import com.example.guitarzone.model.dtos.UserRegistrationDTO;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.repositories.UserRepository;
import com.example.guitarzone.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRegistrationDTO userRegistrationDTO;
    private UserAccountInfoDTO userAccountInfoDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");

        userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setEmail("user@example.com");
        userRegistrationDTO.setFirstName("John");
        userRegistrationDTO.setLastName("Doe");
        userRegistrationDTO.setPassword("password");

        userAccountInfoDTO = new UserAccountInfoDTO();
        userAccountInfoDTO.setFirstName("John");
        userAccountInfoDTO.setLastName("Doe");
        userAccountInfoDTO.setPhoneNumber("1234567890");
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.findByEmail(userRegistrationDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userRegistrationDTO.getPassword())).thenReturn("encodedPassword");
        when(modelMapper.map(userRegistrationDTO, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        boolean result = userService.registerUser(userRegistrationDTO);

        assertTrue(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testRegisterUser_UserAlreadyExists() {
        when(userRepository.findByEmail(userRegistrationDTO.getEmail())).thenReturn(Optional.of(user));

        boolean result = userService.registerUser(userRegistrationDTO);

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFindByEmail_UserFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("user@example.com");

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findByEmail("user@example.com");
    }

    @Test
    void testFindByEmail_UserNotFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.findByEmail("user@example.com");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("user@example.com");
    }

    @Test
    void testUpdateUserAccount_Success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        doNothing().when(modelMapper).map(userAccountInfoDTO, user);
        when(userRepository.save(user)).thenReturn(user);

        userService.updateUserAccount("user@example.com", userAccountInfoDTO);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserAccount_UserNotFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserAccount("user@example.com", userAccountInfoDTO);
        });

        assertEquals("User not found with email: user@example.com", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserName_Success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.updateUserName("user@example.com", "John", "Smith");

        assertEquals("John", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserName_UserNotFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserName("user@example.com", "John", "Smith");
        });

        assertEquals("User not found with email: user@example.com", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserPhone_Success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.updateUserPhone("user@example.com", "1234567890");

        assertEquals("1234567890", user.getPhoneNumber());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserPhone_UserNotFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserPhone("user@example.com", "1234567890");
        });

        assertEquals("User not found with email: user@example.com", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetAccountDetails_UserNotFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getAccountDetails("user@example.com");
        });

        assertEquals("No user found with email user@example.com", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("user@example.com");
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(user.getEmail(), result.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }
}
