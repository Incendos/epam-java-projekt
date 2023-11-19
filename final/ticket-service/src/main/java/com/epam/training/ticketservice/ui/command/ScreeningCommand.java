package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import com.epam.training.ticketservice.core.screening.service.ScreeningService;
import com.epam.training.ticketservice.core.user.service.UserService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
public class ScreeningCommand extends CommandBase {

    private final ScreeningService screeningService;

    public ScreeningCommand(UserService userService, ScreeningService screeningService) {
        super(userService);
        this.screeningService = screeningService;
    }

    @ShellMethodAvailability("isAdmin")
    @ShellMethod(key = "create screening", value = "create screening")
    public String createScreening(String movieTitle, String roomName, String dateTimeString) {
        try {
            LocalDateTime dateTime = LocalDateTime
                    .parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            if (screeningService.createScreening(movieTitle, roomName, dateTime).isPresent()) {
                return "Created screening successfully!";
            }
            return "Failed to create screening";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @ShellMethodAvailability("isAdmin")
    @ShellMethod(key = "delete screening", value = "delete screening")
    public String deleteScreening(String movieTitle, String roomName, String dateTimeString) {
        LocalDateTime dateTime = LocalDateTime
                .parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        if (screeningService.deleteScreening(movieTitle, roomName, dateTime)) {
            return "Deleted screening successfully!";
        }
        return "No such Screening exists!";
    }

    @ShellMethod(key = "list screenings", value = "list screenings")
    public String listScreenings() {
        List<ScreeningDto> screenings = screeningService.listScreenings();
        if (screenings.size() > 0) {
            return screenings.stream()
                    .map((screeningDto) -> screeningDto.movie().toString()
                            + ", screened in room "
                            + screeningDto.room().name()
                            + ", at "
                            + screeningDto.dateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    .collect(Collectors.joining("\n"));
        }
        return "There are no screenings";
    }
}
