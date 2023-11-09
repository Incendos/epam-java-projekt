package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.screening.service.ScreeningService;
import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.User;
import com.epam.training.ticketservice.core.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@ShellComponent
@RequiredArgsConstructor
public class ScreeningCommand {

    private final ScreeningService screeningService;
    private final UserService userService;
    @ShellMethodAvailability("isAdmin")
    @ShellMethod(key = "create screening", value = "create screening")
    public String createScreening(String movieTitle, String roomName, String dateString, String timeString) {
        try {
            LocalDateTime dateTime = LocalDateTime
                    .parse(dateString + " " + timeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            if(screeningService.createScreening(movieTitle, roomName, dateTime).isPresent())
                return "Created screening successfully!";
            return "Failed to create screening";
        }
        catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    Availability isAdmin() {
        Optional<UserDto> user = userService.describe();
        if(user.isPresent() && user.get().role() == User.Role.ADMIN)
            return Availability.available();
        return Availability.unavailable("for Admins only!");
    }
}
