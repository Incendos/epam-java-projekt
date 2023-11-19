package com.epam.training.ticketservice.core.room.model;

import com.epam.training.ticketservice.core.room.persistance.Room;

public record RoomDto(String name, Integer rowCount, Integer columnCount) {

    public RoomDto(Room room) {
        this(room.getName(), room.getRowCount(), room.getColumnCount());
    }
}
