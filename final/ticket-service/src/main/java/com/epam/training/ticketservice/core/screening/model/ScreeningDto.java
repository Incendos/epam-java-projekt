package com.epam.training.ticketservice.core.screening.model;

import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.room.model.RoomDto;
import com.epam.training.ticketservice.core.screening.persistence.Screening;

import java.time.LocalDateTime;

public record ScreeningDto(MovieDto movie, RoomDto room, LocalDateTime dateTime) {
    public ScreeningDto(Screening screening) {
        this(new MovieDto(screening.getMovie()),
                new RoomDto(screening.getRoom()),
                screening.getDateTime());
    }
}
