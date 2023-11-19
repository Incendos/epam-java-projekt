package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.service.BookingService;
import com.epam.training.ticketservice.core.user.persistence.UserRepository;
import com.epam.training.ticketservice.core.user.service.UserService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@ShellComponent
public class BookingCommand extends CommandBase {

    private final BookingService bookingService;
    private final UserService userService;

    public BookingCommand(UserService userService, BookingService bookingService, UserService userService1) {
        super(userService);
        this.bookingService = bookingService;
        this.userService = userService1;
    }

    @ShellMethodAvailability("isUser")
    @ShellMethod(key = "book", value = "book seats")
    public String bookSeats(String movieTitle, String roomName, String dateTimeString, String seats) {
        try {
            LocalDateTime dateTime = LocalDateTime
                    .parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            List<SeatDto> seatList = Arrays.stream(seats.split(" "))
                    .map((seatString) -> seatString.split(","))
                    .map((seatString) -> new SeatDto(parseInt(seatString[0]), parseInt(seatString[1])))
                    .toList();

            BookingDto booking = bookingService.book(userService.describe().get().username(),
                    movieTitle, roomName, dateTime, seatList);
            return "Seats booked: "
                    + seatList.stream().map(SeatDto::toString).collect(Collectors.joining(", "))
                    + "; the price for this booking is "
                    + booking.price()
                    + " HUF";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
}