package com.epam.training.ticketservice.core.room.service;

import com.epam.training.ticketservice.core.room.model.RoomDto;
import com.epam.training.ticketservice.core.room.persistance.Room;
import com.epam.training.ticketservice.core.room.persistance.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    final private RoomRepository roomRepository;

    @Override
    public Optional<RoomDto> createRoom(String name, Integer rowCount, Integer columnCount) {
        if(roomRepository.findByName(name).isPresent())
            return Optional.empty();
        Room room = new Room(name, rowCount, columnCount);
        roomRepository.save(room);
        return Optional.of(new RoomDto(name, rowCount, columnCount));
    }

    @Override
    public Optional<RoomDto> updateRoom(String name, Integer rowCount, Integer columnCount) {
        Optional<Room> room = roomRepository.findByName(name);
        if(room.isPresent())
        {
            room.get().setRowCount(rowCount);
            room.get().setColumnCount(columnCount);
            roomRepository.save(room.get());
            return Optional.of(new RoomDto(name, rowCount, columnCount));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteRoom(String name) {
        Optional<Room> room = roomRepository.findByName(name);
        if(room.isPresent()) {
            roomRepository.delete(room.get());
            return true;
        }
        return false;
    }

    @Override
    public List<RoomDto> listRooms() {
        return roomRepository.findAll()
                .stream()
                .map((room) -> new RoomDto(room.getName(), room.getRowCount(), room.getColumnCount())).toList();
    }
}
