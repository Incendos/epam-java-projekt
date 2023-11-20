package com.epam.training.ticketservice.core.booking.service;

import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.persistence.BookingRepository;
import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponent;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponentRepository;
import com.epam.training.ticketservice.core.room.persistance.Room;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.Screening;
import com.epam.training.ticketservice.core.screening.persistence.ScreeningRepository;
import com.epam.training.ticketservice.core.user.persistence.User;
import com.epam.training.ticketservice.core.user.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

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

    private final BookingService underTest = new BookingServiceImpl(bookingRepository,
            screeningRepository, userRepository, priceComponentRepository);

    @Test
    void testBookShouldReturnDtoWhenItCreatesABooking() {
        Movie movie = new Movie("movie", "genre", Duration.ofMinutes(1));
        Room room = new Room("room", 10, 10);
        LocalDateTime dateTime = LocalDateTime.now();
        Screening screening = new Screening(movie, room, dateTime);

        when(screeningRepository.findByMovieTitleAndRoomNameAndDateTime("movie", "room", dateTime))
                .thenReturn(Optional.of(screening));

        when(bookingRepository.findAllByScreening(ArgumentMatchers.any())).thenReturn(List.of());

        User user = new User("user", "user", User.Role.USER);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        PriceComponent basePriceComp = new PriceComponent("BASE_PRICE_COMPONENT", 1500);
        when(priceComponentRepository.findByName("BASE_PRICE_COMPONENT")).thenReturn(Optional.of(basePriceComp));

        List<SeatDto> seats = List.of(new SeatDto(1,1));
        BookingDto expected = new BookingDto(new ScreeningDto(screening), seats, 1500);

        assertEquals(expected, underTest.book("user", "movie", "room", dateTime, seats));

        verify(screeningRepository)
                .findByMovieTitleAndRoomNameAndDateTime("movie", "room", dateTime);
        verify(bookingRepository).findAllByScreening(ArgumentMatchers.any());
        verify(userRepository).findByUsername("user");
        verify(priceComponentRepository).findByName("BASE_PRICE_COMPONENT");
    }

}