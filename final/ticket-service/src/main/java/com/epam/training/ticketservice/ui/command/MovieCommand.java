package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.movie.service.MovieService;
import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.User;
import com.epam.training.ticketservice.core.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
public class MovieCommand extends CommandBase {

    private final MovieService movieService;

    MovieCommand(MovieService movieService, UserService userService) {
        super(userService);
        this.movieService = movieService;
    }

    @ShellMethodAvailability("isAdmin")
    @ShellMethod(key = "create movie", value = "create movie")
    public String createMovie(String title, String genre, Integer lengthAsMinutes) {
        if (movieService.createMovie(title, genre, Duration.ofMinutes(lengthAsMinutes)).isPresent()) {
            return "Created movie successfully!";
        }
        return "Movie already exists!";
    }

    @ShellMethodAvailability("isAdmin")
    @ShellMethod(key = "update movie", value = "update movie")
    public String updateMovie(String title, String genre, Integer lengthAsMinutes) {
        if (movieService.updateMovie(title, genre, Duration.ofMinutes(lengthAsMinutes)).isPresent()) {
            return "updated movie successfully!";
        }
        return "Movie doesn't exists!";
    }

    @ShellMethodAvailability("isAdmin")
    @ShellMethod(key = "delete movie", value = "delete movie")
    public String deleteMovie(String title) {
        if (movieService.deleteMovie(title)) {
            return "Deleted movie: " + title;
        }
        return "Movie " + title + " doesn't exists!";
    }

    @ShellMethod(key = "list movies", value = "list movies")
    public String listMovies() {
        List<MovieDto> movies = movieService.listMovies();
        if (movies.size() > 0) {
            return movies.stream()
                    .map((movieDto) -> movieDto.title()
                            + " ("
                            + movieDto.genre()
                            + ", "
                            + movieDto.length().toMinutes()
                            + " minutes)")
                    .collect(Collectors.joining("\n"));
        }
        return "There are no movies at the moment";
    }
}
