package com.epam.training.ticketservice.core.booking.service;

import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.persistence.Booking;
import com.epam.training.ticketservice.core.booking.persistence.BookingRepository;
import com.epam.training.ticketservice.core.booking.persistence.Seat;
import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponent;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponentRepository;
import com.epam.training.ticketservice.core.room.persistance.Room;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.Screening;
import com.epam.training.ticketservice.core.screening.persistence.ScreeningRepository;
import com.epam.training.ticketservice.core.user.persistence.User;
import com.epam.training.ticketservice.core.user.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.awt.print.Book;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {
    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final ScreeningRepository screeningRepository = mock(ScreeningRepository.class);
    private final PriceComponentRepository priceComponentRepository = mock(PriceComponentRepository.class);

    private final BookingServiceImpl underTest = new BookingServiceImpl(bookingRepository,
            screeningRepository, userRepository, priceComponentRepository);

    private static Movie movie;
    private static Room room;
    private static LocalDateTime dateTime;
    private static Screening screening;
    private static User user;
    private static User admin;
    private static PriceComponent basePriceComp;
    private static List<SeatDto> seats;
    private static Booking booking;
    private static PriceComponent extraPriceComp;

    @BeforeEach
    void initEntities() {
        movie = new Movie("movie", "genre", Duration.ofMinutes(1));
        room = new Room("room", 10, 10);
        dateTime = LocalDateTime.of(2023,1,1,16,0);
        screening = new Screening(movie, room, dateTime);
        user = new User("user", "user", User.Role.USER);
        admin = new User("admin", "admin", User.Role.ADMIN);
        basePriceComp = new PriceComponent("BASE_PRICE_COMPONENT", 1500);
        seats = List.of(new SeatDto(1,1));
        booking = new Booking(user, screening, List.of(new Seat(5,5),
                new Seat(1, 1)), 1500);
        extraPriceComp = new PriceComponent("extra100", 100);

        movie.setPriceComponents(List.of(extraPriceComp));
        room.setPriceComponents(List.of(extraPriceComp));
        screening.setPriceComponents(List.of(extraPriceComp));
    }

    @Test
    void testBookShouldReturnDtoWhenItCreatesABooking() {
        when(screeningRepository.findByMovieTitleAndRoomNameAndDateTime("movie", "room", dateTime))
                .thenReturn(Optional.of(screening));
        when(bookingRepository.findAllByScreening(ArgumentMatchers.any())).thenReturn(List.of());
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(priceComponentRepository.findByName("BASE_PRICE_COMPONENT")).thenReturn(Optional.of(basePriceComp));

        BookingDto expected = new BookingDto(new ScreeningDto(screening), seats, 1800);
        assertEquals(expected, underTest.book("user", "movie", "room", dateTime, seats));

        verify(screeningRepository, times(2))
                .findByMovieTitleAndRoomNameAndDateTime("movie", "room", dateTime);
        verify(bookingRepository).findAllByScreening(ArgumentMatchers.any());
        verify(userRepository).findByUsername("user");
        verify(priceComponentRepository).findByName("BASE_PRICE_COMPONENT");
    }

    @Test
    void testCheckScreeningDetailsShouldReturnNonEmptyStringWhenScreeningIsEmpty() {
        when(screeningRepository.findByMovieTitleAndRoomNameAndDateTime(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Optional.empty());

        assertEquals("No such screening exists",
                underTest.checkScreeningDetails("movie", "room",
                        dateTime, List.of()));

        verify(screeningRepository).findByMovieTitleAndRoomNameAndDateTime(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void testCheckScreeningDetailsShouldReturnNonEmptyStringWhenASeatDoesNotExistsInTheRoom() {
        when(screeningRepository.findByMovieTitleAndRoomNameAndDateTime("movie", "room", dateTime))
                .thenReturn(Optional.of(screening));

        assertFalse(underTest.checkScreeningDetails(
                "movie", "room", dateTime, List.of(new SeatDto(-5,5))).isEmpty());
        assertFalse(underTest.checkScreeningDetails(
                "movie", "room", dateTime, List.of(new SeatDto(5,-5))).isEmpty());
        assertFalse(underTest.checkScreeningDetails(
                "movie", "room", dateTime, List.of(new SeatDto(111,5))).isEmpty());
        assertFalse(underTest.checkScreeningDetails(
                "movie", "room", dateTime, List.of(new SeatDto(5,111))).isEmpty());

        verify(screeningRepository, times(4))
                .findByMovieTitleAndRoomNameAndDateTime("movie", "room", dateTime);
    }

    @Test
    void testBookShouldThrowExceptionWhenASSeatIsOccupied() {
        when(screeningRepository.findByMovieTitleAndRoomNameAndDateTime("movie", "room", dateTime))
                .thenReturn(Optional.of(screening));
        when(bookingRepository.findAllByScreening(ArgumentMatchers.any())).thenReturn(List.of(booking));

        assertThrows(IllegalArgumentException.class, () -> underTest.book("user",
                "movie", "room", dateTime, seats));

        verify(screeningRepository, times(2))
                .findByMovieTitleAndRoomNameAndDateTime("movie", "room", dateTime);
        verify(bookingRepository).findAllByScreening(ArgumentMatchers.any());
    }

    @Test
    void testBookShouldThrowExceptionWhenUserIsEmpty() {
        when(screeningRepository.findByMovieTitleAndRoomNameAndDateTime("movie", "room", dateTime))
                .thenReturn(Optional.of(screening));
        when(bookingRepository.findAllByScreening(ArgumentMatchers.any())).thenReturn(List.of());
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                underTest.book("user", "movie", "room", dateTime, seats));

        verify(screeningRepository, times(2))
                .findByMovieTitleAndRoomNameAndDateTime("movie", "room", dateTime);
        verify(bookingRepository).findAllByScreening(ArgumentMatchers.any());
        verify(userRepository).findByUsername("user");
    }

    @Test
    void testBookShouldThrowExceptionWhenUserIsAdmin() {
        when(screeningRepository.findByMovieTitleAndRoomNameAndDateTime("movie", "room", dateTime))
                .thenReturn(Optional.of(screening));
        when(bookingRepository.findAllByScreening(ArgumentMatchers.any())).thenReturn(List.of());
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        assertThrows(IllegalArgumentException.class, () ->
                underTest.book("admin", "movie", "room",
                        dateTime, seats));

        verify(screeningRepository, times(2))
                .findByMovieTitleAndRoomNameAndDateTime("movie", "room", dateTime);
        verify(bookingRepository).findAllByScreening(ArgumentMatchers.any());
        verify(userRepository).findByUsername("admin");
    }

    @Test
    void testBookShouldThrowExceptionWhenScreeningCheckFails() {
        when(screeningRepository.findByMovieTitleAndRoomNameAndDateTime(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                underTest.book("user", "movie",
                        "room",
                        dateTime, List.of()));

        verify(screeningRepository).findByMovieTitleAndRoomNameAndDateTime(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void testShowPriceForShouldReturnTheCostOfBookingWhenParametersAreValid() {
        when(screeningRepository.findByMovieTitleAndRoomNameAndDateTime("movie", "room", dateTime))
                .thenReturn(Optional.of(screening));
        when(priceComponentRepository.findByName("BASE_PRICE_COMPONENT")).thenReturn(Optional.of(basePriceComp));

        assertEquals(1800, underTest.showPriceFor( "movie",
                "room", dateTime, seats));

        verify(screeningRepository, times(2))
                .findByMovieTitleAndRoomNameAndDateTime("movie", "room", dateTime);
        verify(priceComponentRepository).findByName("BASE_PRICE_COMPONENT");
    }

    @Test
    void testShowPriceForShouldThrowExceptionWhenScreeningIsEmpty() {
        when(screeningRepository.findByMovieTitleAndRoomNameAndDateTime(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> underTest.showPriceFor("movie",
                "room", dateTime, List.of()));

        verify(screeningRepository).findByMovieTitleAndRoomNameAndDateTime(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void testListBookingsShouldReturnWithTheUserBookings() {
        when(bookingRepository.findAllByUser(user)).thenReturn(List.of(booking));
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        List<BookingDto> expected = List.of(new BookingDto(booking));
        assertEquals(expected, underTest.listBookings("user"));

        verify(bookingRepository).findAllByUser(user);
        verify(userRepository).findByUsername("user");
    }

    @Test
    void testListBookingsShouldThrowExceptionWhenUserIsEmpty() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> underTest.listBookings("user"));

        verify(userRepository).findByUsername("user");
    }

    @Test
    void testListBookingsShouldThrowExceptionWhenUserIsAdmin() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        assertThrows(IllegalArgumentException.class, () -> underTest.listBookings("admin"));

        verify(userRepository).findByUsername("admin");
    }
}