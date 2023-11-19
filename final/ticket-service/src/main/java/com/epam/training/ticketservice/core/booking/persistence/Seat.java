package com.epam.training.ticketservice.core.booking.persistence;

import com.epam.training.ticketservice.core.booking.model.SeatDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "Seats")
@NoArgsConstructor
public class Seat {
    @Id
    @GeneratedValue
    private Integer id;

    Integer rowNumber;
    Integer columnNumber;

    public Seat(int rowNumber, int columnNumber) {
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
    }

    public Seat(SeatDto seatDto) {
        rowNumber = seatDto.row();
        columnNumber = seatDto.column();
    }
}