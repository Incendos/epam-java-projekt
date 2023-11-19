package com.epam.training.ticketservice.core.booking.model;

import com.epam.training.ticketservice.core.booking.persistence.Seat;

public record SeatDto(int row, int column) {
    public SeatDto(Seat seat) {
        this(seat.getRowNumber(), seat.getColumnNumber());
    }

    @Override
    public String toString() {
        return "(" + row + "," + column + ")";
    }
}