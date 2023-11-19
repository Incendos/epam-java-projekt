package com.epam.training.ticketservice.core.user.service;

import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.User;
import com.epam.training.ticketservice.core.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private UserDto loggedInUser = null;

    @Override
    public Optional<UserDto> loginAsAdmin(String username, String password) {
        Optional<User> user = userRepository.findByUsernameAndPassword(username, password);
        if (user.isEmpty() || user.get().getRole() == User.Role.USER) {
            return Optional.empty();
        }
        loggedInUser = new UserDto(user.get().getUsername(), user.get().getRole());
        return describe();
    }

    @Override
    public Optional<UserDto> loginAsNotAdmin(String username, String password) {
        Optional<User> user = userRepository.findByUsernameAndPassword(username, password);
        if (user.isEmpty() || user.get().getRole() == User.Role.ADMIN) {
            return Optional.empty();
        }
        loggedInUser = new UserDto(user.get().getUsername(), user.get().getRole());
        return describe();
    }

    @Override
    public Optional<UserDto> logout() {
        Optional<UserDto> previouslyLoggedInUser = describe();
        loggedInUser = null;
        return previouslyLoggedInUser;
    }

    @Override
    public Optional<UserDto> describe() {
        return Optional.ofNullable(loggedInUser);
    }

    @Override
    public void registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Failed to register: Username is taken");
        }
        User user = new User(username, password, User.Role.USER);
        userRepository.save(user);
    }
}
