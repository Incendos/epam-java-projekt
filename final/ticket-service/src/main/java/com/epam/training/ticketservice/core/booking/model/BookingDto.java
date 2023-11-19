package com.epam.training.ticketservice.core.booking.model;


import com.epam.training.ticketservice.core.booking.persistence.Booking;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record BookingDto(ScreeningDto screening, List<SeatDto> seats, int price) {
    public BookingDto(Booking booking) {
        this(new ScreeningDto(booking.getScreening()),
                booking.getSeats().stream().map(SeatDto::new).toList(),
                booking.getPrice());
    }

    @Override
    public String toString() {
        return "Seats "
                + seats.stream().map(Objects::toString).collect(Collectors.joining(", "))
                + " on "
                + screening.movie().title()
                + " in room "
                + screening.room().name()
                + " starting at "
                + screening.dateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                + " for "
                + price
                + " HUF";
    }
}
