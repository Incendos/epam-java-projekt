package com.epam.training.ticketservice.core.pricecomponent.service;

import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.movie.persistence.MovieRepository;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponent;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponentRepository;
import com.epam.training.ticketservice.core.room.persistance.Room;
import com.epam.training.ticketservice.core.room.persistance.RoomRepository;
import com.epam.training.ticketservice.core.screening.persistence.Screening;
import com.epam.training.ticketservice.core.screening.persistence.ScreeningRepository;
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

    @Test
    void testCreatePriceComponentShouldCreatePriceComponentWhenParametersAreValid() {
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.empty());

        underTest.createPriceComponent("comp", 100);

        verify(priceComponentRepository).findByName("comp");
    }

    @Test
    void testCreatePriceComponentShouldThrowExceptionWhenCompAlreadyExists() {
        PriceComponent priceComponent = new PriceComponent("comp", 100);
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));


        assertThrows(IllegalArgumentException.class, () ->
                underTest.createPriceComponent("comp", 100));

        verify(priceComponentRepository).findByName("comp");
    }

    @Test
    void testInitShouldExecuteWithoutExceptionsWhenBasePriceCompIsPresent() {
        PriceComponent priceComponent = new PriceComponent("BASE_PRICE_COMPONENT", 1500);
        when(priceComponentRepository.findByName("BASE_PRICE_COMPONENT"))
                .thenReturn(Optional.of(priceComponent));

        underTest.checkBasePriceComp();

        verify(priceComponentRepository).findByName("BASE_PRICE_COMPONENT");
    }

    @Test
    void testInitShouldThrowExceptionWhenBasePriceCompIsEmpty() {
        when(priceComponentRepository.findByName("BASE_PRICE_COMPONENT"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                underTest.checkBasePriceComp());

        verify(priceComponentRepository).findByName("BASE_PRICE_COMPONENT");
    }

    @Test
    void testUpdateBasePriceShouldUpdateTheBasePrice() {
        PriceComponent priceComponent = new PriceComponent("BASE_PRICE_COMPONENT", 1500);
        when(priceComponentRepository.findByName("BASE_PRICE_COMPONENT")).thenReturn(Optional.of(priceComponent));

        underTest.updateBasePrice(3000);
        assertEquals(3000, priceComponent.getPrice());

        verify(priceComponentRepository).findByName("BASE_PRICE_COMPONENT");
    }

    @Test
    void testAttachPriceComponentToRoomShouldAttachTheCompToTheRoom() {
        PriceComponent priceComponent = new PriceComponent("comp", 500);
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));

        Room room = new Room("room", 10, 10);
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
        PriceComponent priceComponent = new PriceComponent("comp", 500);
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));

        when(roomRepository.findByName("room")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                underTest.attachPriceComponentToRoom("comp", "room"));

        verify(priceComponentRepository).findByName("comp");
        verify(roomRepository).findByName("room");
    }

    @Test
    void testAttachPriceComponentToMovieShouldAttachTheComponent() {
        PriceComponent priceComponent = new PriceComponent("comp", 500);
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));

        Movie movie = new Movie("movie", "genre", Duration.ofMinutes(1));
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
        PriceComponent priceComponent = new PriceComponent("comp", 500);
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));

        when(movieRepository.findByTitle("movie")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                underTest.attachPriceComponentToMovie("comp", "movie"));

        verify(priceComponentRepository).findByName("comp");
        verify(movieRepository).findByTitle("movie");
    }

    @Test
    void testAttachPriceComponentToScreeningShouldAttachPriceComponentWhenParametersAreValid() {
        PriceComponent priceComponent = new PriceComponent("comp", 500);
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));

        Movie movie = new Movie("movie", "genre", Duration.ofMinutes(100));
        Room room = new Room("room", 10, 10);
        LocalDateTime dateTime = LocalDateTime.now();
        Screening screening = new Screening(movie, room, dateTime);
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
                        "room", LocalDateTime.now()));

        verify(priceComponentRepository).findByName("comp");
    }

    @Test
    void testAttachPriceComponentToScreeningShouldThrowExceptionWhenScreeningIsEmpty() {
        PriceComponent priceComponent = new PriceComponent("comp", 500);
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));

        LocalDateTime dateTime = LocalDateTime.now();
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