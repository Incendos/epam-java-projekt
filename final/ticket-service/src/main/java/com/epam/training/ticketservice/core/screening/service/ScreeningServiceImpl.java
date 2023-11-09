package com.epam.training.ticketservice.core.screening.service;

import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.movie.persistence.MovieRepository;
import com.epam.training.ticketservice.core.room.persistance.Room;
import com.epam.training.ticketservice.core.room.persistance.RoomRepository;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.Screening;
import com.epam.training.ticketservice.core.screening.persistence.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private static final Duration CLEANING_BREAK_DURATION = Duration.ofMinutes(10);
    @Override
    public Optional<ScreeningDto> createScreening(String movieTitle, String roomName, LocalDateTime dateTime) {
        Optional<Movie> movie = movieRepository.findByTitle(movieTitle);
        if(movie.isEmpty())
            throw new IllegalArgumentException("No such movie exists!");

        Optional<Room> room = roomRepository.findByName(roomName);
        if(room.isEmpty())
            throw new IllegalArgumentException("No such room exists!");

        for(Screening screening : screeningRepository.findAllByRoomNameAndDateTimeBefore(roomName, dateTime
                .plus(movie.get().getLength())
                .plus(CLEANING_BREAK_DURATION))) {

            Optional<Movie> screeningMovie = movieRepository.findByTitle(screening.getMovieTitle());

            if(Duration.between(screening.getDateTime(), dateTime)
                    .minus(screeningMovie.get().getLength())
                    .isNegative())
                throw new IllegalArgumentException("There is an overlapping screening");

            if(Duration.between(screening.getDateTime(), dateTime)
                    .minus(screeningMovie.get().getLength()
                            .plus(CLEANING_BREAK_DURATION))
                    .isNegative())
                throw new IllegalArgumentException("This would start in the break period after another screening in this room");
        }

        Screening screening = new Screening(movieTitle, roomName, dateTime);
        screeningRepository.save(screening);
        return Optional.of(new ScreeningDto(movieTitle, roomName, dateTime));
    }
}
