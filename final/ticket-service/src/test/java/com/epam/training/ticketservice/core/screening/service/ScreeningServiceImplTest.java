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
import org.junit.jupiter.api.BeforeEach;
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


    private static Movie movie;
    private static Room room;
    private static LocalDateTime dateTime;
    private static Screening screening;

    @BeforeEach
    void init() {
        movie = new Movie("movie", "genre", Duration.ofMinutes(100));
        room = new Room("room", 10, 10);
        dateTime = LocalDateTime.of(2023,1,1,16,0);
        screening = new Screening(movie, room, dateTime.minus(Duration.ofMinutes(100)));
    }

    @Test
    void testCreateScreeningShouldReturnDtoWhenItCreatesAScreening() {
        when(movieRepository.findByTitle("movie")).thenReturn(Optional.of(movie));
        when(roomRepository.findByName("room")).thenReturn(Optional.of(room));
        Screening notOverlappingScreening = new Screening(movie, room, dateTime.minus(Duration.ofMinutes(200)));
        when(screeningRepository.findAllByRoomAndDateTimeBefore(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(List.of(notOverlappingScreening));

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
                underTest.createScreening("movie", "room", dateTime));

        verify(movieRepository).findByTitle(ArgumentMatchers.any());
    }

    @Test
    void testCreateScreeningShouldThrowExceptionWhenRoomIsEmpty() {
        when(movieRepository.findByTitle("movie")).thenReturn(Optional.of(movie));
        when(roomRepository.findByName(ArgumentMatchers.any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                underTest.createScreening("movie", "room", dateTime));

        verify(movieRepository).findByTitle("movie");
        verify(roomRepository).findByName(ArgumentMatchers.any());
    }

    @Test
    void testCreateScreeningShouldThrowExceptionDuringOverLappingScreening() {
        when(movieRepository.findByTitle("movie")).thenReturn(Optional.of(movie));
        when(roomRepository.findByName("room")).thenReturn(Optional.of(room));
        Screening overlappingScreening = new Screening(movie, room, dateTime);
        when(screeningRepository.findAllByRoomAndDateTimeBefore(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(List.of(overlappingScreening));

        assertThrows(IllegalArgumentException.class, () ->
                underTest.createScreening("movie", "room", dateTime));

        verify(movieRepository).findByTitle("movie");
        verify(roomRepository).findByName("room");
        verify(screeningRepository).findAllByRoomAndDateTimeBefore(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void testCreateScreeningShouldThrowExceptionDuringOverLappingCleaning() {
        when(movieRepository.findByTitle("movie")).thenReturn(Optional.of(movie));
        when(roomRepository.findByName("room")).thenReturn(Optional.of(room));
        Screening overlappingScreening = new Screening(movie, room, dateTime.minus(Duration.ofMinutes(105)));
        when(screeningRepository.findAllByRoomAndDateTimeBefore(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(List.of(overlappingScreening));

        assertThrows(IllegalArgumentException.class, () ->
                underTest.createScreening("movie", "room", dateTime));

        verify(movieRepository).findByTitle("movie");
        verify(roomRepository).findByName("room");
        verify(screeningRepository).findAllByRoomAndDateTimeBefore(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void testDeleteScreeningShouldReturnTrueWhenItDeletesScreening() {
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

        assertFalse(underTest.deleteScreening("movie", "room",
                dateTime));

        verify(screeningRepository).findByMovieTitleAndRoomNameAndDateTime(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void testListScreeningsShouldReturnAListOfScreenings() {
        when(screeningRepository.findAll()).thenReturn(List.of(screening));

        List<ScreeningDto> expected = List.of(new ScreeningDto(screening));
        assertEquals(expected, underTest.listScreenings());

        verify(screeningRepository).findAll();
    }
}