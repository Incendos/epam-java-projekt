package com.epam.training.ticketservice.core.pricecomponent.service;

import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.movie.persistence.MovieRepository;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponent;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponentRepository;
import com.epam.training.ticketservice.core.room.persistance.Room;
import com.epam.training.ticketservice.core.room.persistance.RoomRepository;
import com.epam.training.ticketservice.core.screening.persistence.Screening;
import com.epam.training.ticketservice.core.screening.persistence.ScreeningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PriceComponentServiceImplTest {
    final private RoomRepository roomRepository = mock(RoomRepository.class);
    final private MovieRepository movieRepository = mock(MovieRepository.class);
    final private ScreeningRepository screeningRepository = mock(ScreeningRepository.class);
    final private PriceComponentRepository priceComponentRepository = mock(PriceComponentRepository.class);

    final private PriceComponentServiceImpl underTest =
            new PriceComponentServiceImpl(priceComponentRepository, roomRepository,
                    movieRepository, screeningRepository);


    private static PriceComponent priceComponent;
    private static PriceComponent basePriceComp;
    private static Room room;
    private static Movie movie;
    private static LocalDateTime dateTime;
    private static Screening screening;


    @BeforeEach
    void init() {
        priceComponent = new PriceComponent("comp", 100);
        basePriceComp = new PriceComponent("BASE_PRICE_COMP", 1500);
        room = new Room("room", 10, 10);
        movie = new Movie("movie", "genre", Duration.ofMinutes(1));
        dateTime = LocalDateTime.of(2023,1,1,16,0);
        screening = new Screening(movie, room, dateTime);
    }

    @Test
    void testCreatePriceComponentShouldCreatePriceComponentWhenParametersAreValid() {
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.empty());

        underTest.createPriceComponent("comp", 100);

        verify(priceComponentRepository).findByName("comp");
    }

    @Test
    void testCreatePriceComponentShouldThrowExceptionWhenCompAlreadyExists() {
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));

        assertThrows(IllegalArgumentException.class, () ->
                underTest.createPriceComponent("comp", 100));

        verify(priceComponentRepository).findByName("comp");
    }

    @Test
    void testUpdateBasePriceShouldUpdateTheBasePrice() {
        when(priceComponentRepository.findByName("BASE_PRICE_COMPONENT")).thenReturn(Optional.of(basePriceComp));

        underTest.updateBasePrice(3000);
        assertEquals(3000, basePriceComp.getPrice());

        verify(priceComponentRepository).findByName("BASE_PRICE_COMPONENT");
    }

    @Test
    void testAttachPriceComponentToRoomShouldAttachTheCompToTheRoom() {
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));
        when(roomRepository.findByName("room")).thenReturn(Optional.of(room));

        underTest.attachPriceComponentToRoom("comp", "room");

        List<PriceComponent> expected = List.of(priceComponent);
        assertEquals(expected, room.getPriceComponents());

        verify(priceComponentRepository).findByName("comp");
        verify(roomRepository).findByName("room");
    }

    @Test
    void testAttachPriceComponentToRoomShouldThrowExceptionWhenPriceComponentIsEmpty() {
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                underTest.attachPriceComponentToRoom("comp", "room"));

        verify(priceComponentRepository).findByName("comp");
    }

    @Test
    void testAttachPriceComponentToRoomShouldThrowExceptionWhenRoomIsEmpty() {
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));
        when(roomRepository.findByName("room")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                underTest.attachPriceComponentToRoom("comp", "room"));

        verify(priceComponentRepository).findByName("comp");
        verify(roomRepository).findByName("room");
    }

    @Test
    void testAttachPriceComponentToMovieShouldAttachTheComponent() {
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));
        when(movieRepository.findByTitle("movie")).thenReturn(Optional.of(movie));

        underTest.attachPriceComponentToMovie("comp", "movie");

        List<PriceComponent> expected = List.of(priceComponent);
        assertEquals(expected, movie.getPriceComponents());

        verify(priceComponentRepository).findByName("comp");
        verify(movieRepository).findByTitle("movie");
    }

    @Test
    void testAttachPriceComponentToMovieShouldThrowExceptionWhenPriceComponentIsEmpty() {
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                underTest.attachPriceComponentToMovie("comp", "movie"));

        verify(priceComponentRepository).findByName("comp");
    }

    @Test
    void testAttachPriceComponentToMovieShouldThrowExceptionWhenMovieIsEmpty() {
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));
        when(movieRepository.findByTitle("movie")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                underTest.attachPriceComponentToMovie("comp", "movie"));

        verify(priceComponentRepository).findByName("comp");
        verify(movieRepository).findByTitle("movie");
    }

    @Test
    void testAttachPriceComponentToScreeningShouldAttachPriceComponentWhenParametersAreValid() {
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));
        when(screeningRepository.findByMovieTitleAndRoomNameAndDateTime("movie", "room",
                dateTime)).thenReturn(Optional.of(screening));

        underTest.attachPriceComponentToScreening("comp", "movie",
                "room", dateTime);

        List<PriceComponent> expected = List.of(priceComponent);
        assertEquals(expected, screening.getPriceComponents());

        verify(priceComponentRepository).findByName("comp");
        verify(screeningRepository).findByMovieTitleAndRoomNameAndDateTime("movie",
                "room", dateTime);
    }

    @Test
    void testAttachPriceComponentToScreeningShouldThrowExceptionWhenPriceComponentIsEmpty() {
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                underTest.attachPriceComponentToScreening("comp", "movie",
                        "room", dateTime));

        verify(priceComponentRepository).findByName("comp");
    }

    @Test
    void testAttachPriceComponentToScreeningShouldThrowExceptionWhenScreeningIsEmpty() {
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));
        when(screeningRepository.findByMovieTitleAndRoomNameAndDateTime("movie", "room",
                dateTime)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                underTest.attachPriceComponentToScreening("comp", "movie",
                        "room", dateTime));

        verify(priceComponentRepository).findByName("comp");
        verify(screeningRepository).findByMovieTitleAndRoomNameAndDateTime("movie",
                "room", dateTime);
    }
}