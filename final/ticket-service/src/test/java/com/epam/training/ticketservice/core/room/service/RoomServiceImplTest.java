package com.epam.training.ticketservice.core.room.service;

import com.epam.training.ticketservice.core.room.model.RoomDto;
import com.epam.training.ticketservice.core.room.persistance.Room;
import com.epam.training.ticketservice.core.room.persistance.RoomRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomServiceImplTest {

    private final RoomRepository roomRepository = mock(RoomRepository.class);
    private final RoomService underTest = new RoomServiceImpl(roomRepository);

    @Test
    void testCreateRoomShouldReturnTheRoomDtoWhenItCreatesARoom() {
        when(roomRepository.findByName(ArgumentMatchers.any())).thenReturn(Optional.empty());
        RoomDto expected = new RoomDto("room", 10, 10);

        assertEquals(expected, underTest.createRoom("room", 10, 10).get());

        verify(roomRepository).findByName(ArgumentMatchers.any());
    }

    @Test
    void testCreateRoomShouldReturnEmptyWhenItDoesNotCreatesARoom() {
        Room room = new Room("room", 10, 10);
        when(roomRepository.findByName("room")).thenReturn(Optional.of(room));

        assertTrue(underTest.createRoom("room", 10, 10).isEmpty());

        verify(roomRepository).findByName("room");
    }

    @Test
    void testUpdateRoomShouldReturnTheRoomDtoWhenItUpdatesARoom() {
        Room room = new Room("room", 10, 10);
        when(roomRepository.findByName("room")).thenReturn(Optional.of(room));

        RoomDto expected = new RoomDto("room", 11, 11);

        assertEquals(expected, underTest.updateRoom("room", 11, 11).get());

        verify(roomRepository).findByName("room");
    }

    @Test
    void testUpdateRoomShouldReturnEmptyWhenItDidNotUpdatesARoom() {
        when(roomRepository.findByName(ArgumentMatchers.any())).thenReturn(Optional.empty());

        assertTrue(underTest.updateRoom("room", 10, 10).isEmpty());

        verify(roomRepository).findByName(ArgumentMatchers.any());
    }

    @Test
    void testDeleteRoomShouldReturnTrueIfItDeleted() {
        Room room = new Room("room", 10, 10);
        when(roomRepository.findByName("room")).thenReturn(Optional.of(room));

        assertTrue(underTest.deleteRoom("room"));

        verify(roomRepository).findByName("room");
    }

    @Test
    void testDeleteRoomShouldReturnFalseWhenItDoesNotDeleted() {
        when(roomRepository.findByName(ArgumentMatchers.any())).thenReturn(Optional.empty());

        assertFalse(underTest.deleteRoom("room"));

        verify(roomRepository).findByName(ArgumentMatchers.any());
    }

    @Test
    void testListRoomsShouldReturnTheListOfRooms() {
        Room room = new Room("room", 10, 10);
        when(roomRepository.findAll()).thenReturn(List.of(room));

        List<RoomDto> expected = List.of(new RoomDto("room", 10, 10));
        assertEquals(expected, underTest.listRooms());

        verify(roomRepository).findAll();
    }

}