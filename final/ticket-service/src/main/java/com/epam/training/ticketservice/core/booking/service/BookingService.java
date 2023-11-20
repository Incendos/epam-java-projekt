package com.epam.training.ticketservice.core.booking.service;

import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.persistence.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    public BookingDto book(String userName, String movieTitle, String roomName,
                           LocalDateTime dateTime, List<SeatDto> seats);

    public List<BookingDto> listBookings(String userName);

    public Integer showPriceFor(String movieTitle, String roomName,
                                LocalDateTime dateTime, List<SeatDto> seats);
}
