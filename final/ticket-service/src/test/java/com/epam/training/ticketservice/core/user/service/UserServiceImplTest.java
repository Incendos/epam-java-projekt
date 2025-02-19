package com.epam.training.ticketservice.core.user.service;

import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.User;
import com.epam.training.ticketservice.core.user.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);

    private final UserService underTest = new UserServiceImpl(userRepository);

    private static User admin;
    private static  User user;

    @BeforeEach
    void init() {
        admin = new User("admin", "admin", User.Role.ADMIN);
        user = new User("user", "user", User.Role.USER);
    }

    @Test
    void testLoginAsAdminShouldAllowLoggingIntoAnExistingAdminAccount() {
        when(userRepository.findByUsernameAndPassword("admin", "admin"))
                .thenReturn(Optional.of(admin));

        Optional<User> expected = Optional.of(admin);
        Optional<UserDto> actual = underTest.loginAsAdmin("admin", "admin");

        assertEquals(expected.get().getUsername(), actual.get().username());
        assertEquals(expected.get().getRole(), actual.get().role());

        verify(userRepository).findByUsernameAndPassword("admin", "admin");
    }

    @Test
    void testLoginAsAdminShouldNotAllowCommonUsersToLogin() {
        when(userRepository.findByUsernameAndPassword("user", "user"))
                .thenReturn(Optional.of(user));

        Optional<UserDto> actual = underTest.loginAsAdmin("user", "user");
        assertTrue(actual.isEmpty());

        verify(userRepository).findByUsernameAndPassword("user", "user");
    }

    @Test
    void testLoginAsNotAdminShouldReturnOptionalOfEmptyIfAccountNotFound() {
        when(userRepository.findByUsernameAndPassword("user", "user"))
                .thenReturn(Optional.empty());

        Optional<UserDto> actual = underTest.loginAsNotAdmin("user", "user");
        assertTrue(actual.isEmpty());

        verify(userRepository).findByUsernameAndPassword("user", "user");
    }

    @Test
    void testLoginAsAdminShouldReturnOptionalOfEmptyIfAccountNotFound() {
        when(userRepository.findByUsernameAndPassword("admin", "admin"))
                .thenReturn(Optional.empty());

        Optional<UserDto> actual = underTest.loginAsAdmin("admin", "admin");
        assertTrue(actual.isEmpty());

        verify(userRepository).findByUsernameAndPassword("admin", "admin");
    }

    @Test
    void testLoginAsNotAdminShouldAllowLoggingIntoAnExistingNotAdminAccount() {
        when(userRepository.findByUsernameAndPassword("user", "user")).thenReturn(Optional.of(user));

        Optional<User> expected = Optional.of(user);
        Optional<UserDto> actual = underTest.loginAsNotAdmin("user", "user");

        assertEquals(expected.get().getUsername(), actual.get().username());
        assertEquals(expected.get().getRole(), actual.get().role());

        verify(userRepository).findByUsernameAndPassword("user", "user");
    }

    @Test
    void testLoginAsNotAdminShouldNotAllowAdminUsersToLogin() {
        when(userRepository.findByUsernameAndPassword("admin", "admin"))
                .thenReturn(Optional.of(admin));

        Optional<UserDto> actual = underTest.loginAsNotAdmin("admin", "admin");
        assertTrue(actual.isEmpty());

        verify(userRepository).findByUsernameAndPassword("admin", "admin");
    }

    @Test
    void testLogoutShouldReturnTheLoggedOutUser() {
        when(userRepository.findByUsernameAndPassword("admin", "admin"))
                .thenReturn(Optional.of(admin));

        Optional<UserDto> expected = underTest.loginAsAdmin("admin", "admin");
        Optional<UserDto> actual = underTest.logout();

        assertEquals(expected, actual);

        verify(userRepository).findByUsernameAndPassword("admin", "admin");
    }

    @Test
    void testRegisterUserShouldThrowExceptionIfRegisteringFailed() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> underTest.registerUser("user", "user"));

        verify(userRepository).findByUsername("user");
    }

    @Test
    void testRegisterUserShouldCallUserRepositoryWhenTheInputIsValid() {
        underTest.registerUser("user", "user");

        verify(userRepository).save(new User("user", "user", User.Role.USER));
    }
}