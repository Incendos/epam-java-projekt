package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.pricecomponent.service.PriceComponentService;
import com.epam.training.ticketservice.core.user.service.UserService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ShellComponent
public class PriceComponentCommand extends CommandBase {

    private final PriceComponentService priceComponentService;

    public PriceComponentCommand(UserService userService, PriceComponentService priceComponentService) {
        super(userService);
        this.priceComponentService = priceComponentService;
    }

    @ShellMethodAvailability("isAdmin")
    @ShellMethod(key = "create price component", value = "create price component")
    public String createPriceComponent(String name, Integer price) {
        try {
            priceComponentService.createPriceComponent(name, price);
            return "Price Component created";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @ShellMethodAvailability("isAdmin")
    @ShellMethod(key = "update base price", value = "update base price")
    public String updateBasePrice(Integer price) {
        priceComponentService.updateBasePrice(price);
        return "Base price updated";
    }

    @ShellMethodAvailability("isAdmin")
    @ShellMethod(key = "attach price component to room", value = "attach price component to room")
    public String attachPriceComponentToRoom(String priceComponentName, String roomName) {
        try {
            priceComponentService.attachPriceComponentToRoom(priceComponentName, roomName);
            return "Price Component attached to room";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @ShellMethodAvailability("isAdmin")
    @ShellMethod(key = "attach price component to movie", value = "attach price component to movie")
    public String attachPriceComponentToMovie(String priceComponentName, String movieTitle) {
        try {
            priceComponentService.attachPriceComponentToMovie(priceComponentName, movieTitle);
            return "Price Component attached to movie";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @ShellMethodAvailability("isAdmin")
    @ShellMethod(key = "attach price component to screening", value = "attach price component to movie")
    public String attachPriceComponentToMovie(String priceComponentName, String movieTitle, String roomName,
                                              String dateTimeString) {
        try {
            LocalDateTime dateTime = LocalDateTime
                    .parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            priceComponentService.attachPriceComponentToScreening(priceComponentName, movieTitle, roomName, dateTime);
            return "Price Component attached to screening";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
}
