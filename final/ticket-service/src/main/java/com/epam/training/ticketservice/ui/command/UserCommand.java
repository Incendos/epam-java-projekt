package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.service.BookingService;
import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.User;
import com.epam.training.ticketservice.core.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class UserCommand {

    private final UserService userService;
    private final BookingService bookingService;

    @ShellMethod(key = "sign out", value = "Account sign out")
    public String signOut() {
        return userService.logout()
                .map(userDto -> userDto.username() + " is logged out!")
                .orElse("You are not signed in");
    }

    @ShellMethod(key = "sign in privileged", value = "Admin sign in")
    public String signInAsAdmin(String userName, String password) {
        return userService.loginAsAdmin(userName, password)
                .map(userDto -> "Signed in with privileged account '" + userDto.username() + "'")
                .orElse("Login failed due to incorrect credentials");
    }

    @ShellMethod(key = "sign in", value = "sign in")
    public String signInAsNotAdmin(String userName, String password) {
        return userService.loginAsNotAdmin(userName, password)
                .map(userDto -> "Signed in with account '" + userDto.username() + "'")
                .orElse("Login failed due to incorrect credentials");
    }

    @ShellMethod(key = "sign up", value = "sign up")
    public String signUp(String userName, String password) {
        try {
            userService.registerUser(userName, password);
            return "Registered successfully!";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @ShellMethod(key = "describe account", value = "describe logged in account")
    public String describeAccount() {
        Optional<UserDto> user = userService.describe();
        if (user.isEmpty()) {
            return "You are not signed in";
        }

        if (user.get().role() == User.Role.ADMIN) {
            return "Signed in with privileged account '" + user.get().username() + "'";
        }

        List<BookingDto> bookings = bookingService.listBookings(user.get().username());
        if (bookings.size() == 0) {
            return "Signed in with account '" + user.get().username() + "'\n"
                    + "You have not booked any tickets yet";
        }

        return "Signed in with account '"
                + user.get().username()
                + "'\n"
                + "Your previous bookings are\n"
                + bookings.stream()
                .map(Objects::toString)
                .collect(Collectors.joining("\n"));
    }
}