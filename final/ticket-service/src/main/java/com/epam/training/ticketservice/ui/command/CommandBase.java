package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.User;
import com.epam.training.ticketservice.core.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;

import java.util.Optional;

@RequiredArgsConstructor()
public abstract class CommandBase {


    private final UserService userService;

    public Availability isAdmin() {
        Optional<UserDto> user = userService.describe();
        if (user.isPresent() && user.get().role() == User.Role.ADMIN) {
            return Availability.available();
        }
        return Availability.unavailable("for Admins only!");
    }

    public Availability isUser() {
        Optional<UserDto> user = userService.describe();
        if (user.isPresent() && user.get().role() == User.Role.USER) {
            return Availability.available();
        }
        return Availability.unavailable("for not admin users only!");
    }
}