package com.epam.training.ticketservice.core.screening.service;

import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.movie.persistence.MovieRepository;
import com.epam.training.ticketservice.core.room.model.RoomDto;
import com.epam.training.ticketservice.core.room.persistance.Room;
import com.epam.training.ticketservice.core.room.persistance.RoomRepository;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import com.epam.training.ticketservice.core.screening.persistence.Screening;
import com.epam.training.ticketservice.core.screening.persistence.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    public static final Duration CLEANING_BREAK_DURATION = Duration.ofMinutes(10);

    @Override
    public Optional<ScreeningDto> createScreening(String movieTitle, String roomName, LocalDateTime dateTime) {
        Optional<Movie> movie = movieRepository.findByTitle(movieTitle);
        if (movie.isEmpty()) {
            throw new IllegalArgumentException("No such movie exists!");
        }

        Optional<Room> room = roomRepository.findByName(roomName);
        if (room.isEmpty()) {
            throw new IllegalArgumentException("No such room exists!");
        }

        for (Screening screening : screeningRepository.findAllByRoomAndDateTimeBefore(room.get(), dateTime
                .plus(movie.get().getLength())
                .plus(CLEANING_BREAK_DURATION))) {

            Movie screeningMovie = screening.getMovie();

            if (Duration.between(screening.getDateTime(), dateTime)
                    .minus(screeningMovie.getLength())
                    .isNegative()) {
                throw new IllegalArgumentException("There is an overlapping screening");
            }

            if (Duration.between(screening.getDateTime(), dateTime)
                    .minus(screeningMovie.getLength()
                            .plus(CLEANING_BREAK_DURATION))
                    .isNegative()) {
                throw new IllegalArgumentException("This would start in the break period "
                        + "after another screening in this room");
            }
        }

        Screening screening = new Screening(movie.get(), room.get(), dateTime);
        screeningRepository.save(screening);

        return Optional.of(new ScreeningDto(
                new MovieDto(movie.get().getTitle(),
                        movie.get().getGenre(),
                        movie.get().getLength()),
                new RoomDto(room.get().getName(),
                        room.get().getRowCount(),
                        room.get().getColumnCount()),
                dateTime));
    }

    @Override
    public boolean deleteScreening(String movieTitle, String roomName, LocalDateTime dateTime) {
        Optional<Screening> screening = screeningRepository.findByMovieTitleAndRoomNameAndDateTime(movieTitle,
                roomName, dateTime);
        if (screening.isPresent()) {
            screeningRepository.delete(screening.get());
            return true;
        }
        return false;
    }

    @Override
    public List<ScreeningDto> listScreenings() {
        return screeningRepository.findAll().stream()
                .map((screening -> new ScreeningDto(
                        new MovieDto(screening.getMovie().getTitle(),
                                screening.getMovie().getGenre(),
                                screening.getMovie().getLength()),
                        new RoomDto(screening.getRoom().getName(),
                                screening.getRoom().getRowCount(),
                                screening.getRoom().getColumnCount()),
                        screening.getDateTime()
        ))).toList();
    }
}
