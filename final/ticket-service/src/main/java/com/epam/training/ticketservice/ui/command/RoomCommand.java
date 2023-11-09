package com.epam.training.ticketservice.ui.command;


import com.epam.training.ticketservice.core.room.model.RoomDto;
import com.epam.training.ticketservice.core.room.service.RoomService;
import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.User;
import com.epam.training.ticketservice.core.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class RoomCommand {
    private final RoomService roomService;
    private final UserService userService;

    @ShellMethodAvailability("isAdmin")
    @ShellMethod(key = "create room", value = "create room")
    public String createRoom(String name, Integer rowCount, Integer columnCount) {
        if(roomService.createRoom(name, rowCount, columnCount).isPresent())
            return "Created room successfully!";
        return "Room already exists!";
    }

    @ShellMethodAvailability("isAdmin")
    @ShellMethod(key = "update room", value = "update room")
    public String updateRoom(String name, Integer rowCount, Integer columnCount) {
        if(roomService.updateRoom(name, rowCount, columnCount).isPresent())
            return "updated room successfully!";
        return "Room does not exists!";
    }

    @ShellMethodAvailability("isAdmin")
    @ShellMethod(key = "delete room", value = "delete room")
    public String deleteRoom(String name) {
        if(roomService.deleteRoom(name))
            return "Deleted movie successfully!";
        return "Movie doesn't exists!";
    }

    @ShellMethod(key = "list rooms", value = "list rooms")
    public String listMovies() {
        List<RoomDto> rooms = roomService.listRooms();
        if(rooms.size() > 0) {
            return rooms.stream()
                    .map((roomDto) -> "Room "
                            + roomDto.name()
                            + " with "
                            + roomDto.rowCount()*roomDto.columnCount()
                            + " seats, "
                            + roomDto.rowCount()
                            + " rows and "
                            + roomDto.columnCount()
                            + " columns")
                    .collect(Collectors.joining("\n"));
        }
        return "There are no rooms at the moment";
    }

    Availability isAdmin() {
        Optional<UserDto> user = userService.describe();
        if(user.isPresent() && user.get().role() == User.Role.ADMIN)
            return Availability.available();
        return Availability.unavailable("for Admins only!");
    }
}
