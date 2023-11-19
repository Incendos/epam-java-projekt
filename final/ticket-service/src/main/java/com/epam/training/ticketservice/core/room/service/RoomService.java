package com.epam.training.ticketservice.core.room.service;

import com.epam.training.ticketservice.core.room.model.RoomDto;
import java.util.List;
import java.util.Optional;

public interface RoomService {

    Optional<RoomDto> createRoom(String name, Integer rowCount, Integer columnCount);

    Optional<RoomDto> updateRoom(String name, Integer rowCount, Integer columnCount);

    boolean deleteRoom(String name);

    List<RoomDto> listRooms();
}
