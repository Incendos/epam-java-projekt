package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class UserCommand {

    private final UserService userService;

    @ShellMethod(key = "sign out", value = "User sign out")
    public String signOut() {
        return userService.logout()
                .map(userDto -> userDto.username() + " is logged out!")
                .orElse("You need to login first!");
    }

    @ShellMethod(key = "sign in privileged", value = "Admin sign in")
    public String signInAsAdmin(String userName, String password) {
        return userService.login(userName, password)
                .map(userDto -> "Signed in with privileged account " + userDto.username())
                .orElse("Login failed due to incorrect credentials");
    }
}