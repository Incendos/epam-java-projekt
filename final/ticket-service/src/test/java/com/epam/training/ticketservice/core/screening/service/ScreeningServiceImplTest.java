package com.epam.training.ticketservice.core.screening.service;

import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.movie.persistence.MovieRepository;
import com.epam.training.ticketservice.core.room.model.RoomDto;
import com.epam.training.ticketservice.core.room.persistance.Room;
import com.epam.training.ticketservice.core.room.persistance.RoomRepository;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.Screening;
import com.epam.training.ticketservice.core.screening.persistence.ScreeningRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScreeningServiceImplTest {

    private final ScreeningRepository screeningRepository = mock(ScreeningRepository.class);
    private final MovieRepository movieRepository = mock(MovieRepository.class);
    private final RoomRepository roomRepository = mock(RoomRepository.class);
    private final ScreeningService underTest = new ScreeningServiceImpl(screeningRepository,
            movieRepository, roomRepository);



    @Test
    void testCreateScreeningShouldReturnDtoWhenItCreatesAScreening() {
        Movie movie = new Movie("movie", "genre", Duration.ofMinutes(1));
        when(movieRepository.findByTitle("movie")).thenReturn(Optional.of(movie));

        Room room = new Room("room", 10, 10);
        when(roomRepository.findByName("room")).thenReturn(Optional.of(room));

        LocalDateTime dateTime = LocalDateTime.now();
        Screening screening = new Screening(movie, room, dateTime.minus(Duration.ofMinutes(100)));
        when(screeningRepository.findAllByRoomAndDateTimeBefore(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(List.of(screening));

        ScreeningDto expected = new ScreeningDto(new MovieDto(movie), new RoomDto(room), dateTime);

        assertEquals(expected, underTest.createScreening("movie", "room", dateTime).get());

        verify(movieRepository).findByTitle("movie");
        verify(roomRepository).findByName("room");
        verify(screeningRepository).findAllByRoomAndDateTimeBefore(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void testCreateScreeningShouldThrowExceptionWhenMovieIsEmpty() {
        when(movieRepository.findByTitle(ArgumentMatchers.any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                underTest.createScreening("movie", "room", LocalDateTime.now()));

        verify(movieRepository).findByTitle(ArgumentMatchers.any());
    }

    @Test
    void testCreateScreeningShouldThrowExceptionWhenRoomIsEmpty() {
        Movie movie = new Movie("movie", "genre", Duration.ofMinutes(1));
        when(movieRepository.findByTitle("movie")).thenReturn(Optional.of(movie));

        when(roomRepository.findByName(ArgumentMatchers.any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                underTest.createScreening("movie", "room", LocalDateTime.now()));

        verify(movieRepository).findByTitle("movie");
        verify(roomRepository).findByName(ArgumentMatchers.any());
    }

    @Test
    void testCreateScreeningShouldThrowExceptionDuringOverLappingScreening() {
        Movie movie = new Movie("movie", "genre", Duration.ofMinutes(1));
        when(movieRepository.findByTitle("movie")).thenReturn(Optional.of(movie));

        Room room = new Room("room", 10, 10);
        when(roomRepository.findByName("room")).thenReturn(Optional.of(room));

        LocalDateTime dateTime = LocalDateTime.now();
        Screening screening = new Screening(movie, room, dateTime);
        when(screeningRepository.findAllByRoomAndDateTimeBefore(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(List.of(screening));

        assertThrows(IllegalArgumentException.class, () ->
                underTest.createScreening("movie", "room", dateTime));

        verify(movieRepository).findByTitle("movie");
        verify(roomRepository).findByName("room");
        verify(screeningRepository).findAllByRoomAndDateTimeBefore(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void testCreateScreeningShouldThrowExceptionDuringOverLappingCleaning() {
        Movie movie = new Movie("movie", "genre", Duration.ofMinutes(1));
        when(movieRepository.findByTitle("movie")).thenReturn(Optional.of(movie));

        Room room = new Room("room", 10, 10);
        when(roomRepository.findByName("room")).thenReturn(Optional.of(room));

        LocalDateTime dateTime = LocalDateTime.now();
        Screening screening = new Screening(movie, room, dateTime);
        when(screeningRepository.findAllByRoomAndDateTimeBefore(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(List.of(screening));

        assertThrows(IllegalArgumentException.class, () ->
                underTest.createScreening("movie", "room", dateTime.plus(Duration.ofMinutes(5))));

        verify(movieRepository).findByTitle("movie");
        verify(roomRepository).findByName("room");
        verify(screeningRepository).findAllByRoomAndDateTimeBefore(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void testDeleteScreeningShouldReturnTrueWhenItDeletesScreening() {
        Movie movie = new Movie("movie", "genre", Duration.ofMinutes(1));
        Room room = new Room("room", 10, 10);
        LocalDateTime dateTime = LocalDateTime.now();

        Screening screening = new Screening(movie, room, dateTime);

        when(screeningRepository.findByMovieTitleAndRoomNameAndDateTime("movie",
                "room", dateTime)).thenReturn(Optional.of(screening));

        assertTrue(underTest.deleteScreening("movie", "room", dateTime));

        verify(screeningRepository).findByMovieTitleAndRoomNameAndDateTime("movie",
                "room", dateTime);
    }

    @Test
    void testDeleteScreeningShouldReturnFalseWhenItDoesNotDeletesScreening() {
        when(screeningRepository.findByMovieTitleAndRoomNameAndDateTime(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Optional.empty());

        assertFalse(underTest.deleteScreening("movie", "room", LocalDateTime.now()));

        verify(screeningRepository).findByMovieTitleAndRoomNameAndDateTime(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void testListScreeningsShouldReturnAListOfScreenings() {
        Movie movie = new Movie("movie", "genre", Duration.ofMinutes(1));
        Room room = new Room("room", 10, 10);
        LocalDateTime dateTime = LocalDateTime.now();

        Screening screening = new Screening(movie, room, dateTime);

        when(screeningRepository.findAll()).thenReturn(List.of(screening));

        List<ScreeningDto> expected = List.of(new ScreeningDto(screening));

        assertEquals(expected, underTest.listScreenings());

        verify(screeningRepository).findAll();
    }
}