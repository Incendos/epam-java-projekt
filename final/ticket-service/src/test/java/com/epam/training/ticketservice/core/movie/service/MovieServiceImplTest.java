package com.epam.training.ticketservice.core.movie.service;

import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.movie.persistence.MovieRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import javax.validation.constraints.AssertTrue;
import java.time.Duration;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieServiceImplTest {

    private final MovieRepository movieRepository = mock(MovieRepository.class);
    private final MovieService underTest = new MovieServiceImpl(movieRepository);

    @Test
    void testCreateMovieShouldCreateMovieWithValidParameters() {
        when(movieRepository.findByTitle(ArgumentMatchers.any())).thenReturn(Optional.empty());

        MovieDto expected = new MovieDto("Movie", "genre", Duration.ofMinutes(1));
        assertEquals(expected, underTest.createMovie("Movie", "genre", Duration.ofMinutes(1)).get());
        verify(movieRepository).findByTitle(ArgumentMatchers.any());
    }

    @Test
    void testCreateMovieShouldReturnEmptyWhenItAlreadyExists() {
        Movie movie = new Movie("movie", "genre", Duration.ofMinutes(1));
        when(movieRepository.findByTitle("movie")).thenReturn(Optional.of(movie));

        assertTrue(underTest.createMovie("movie", "genre", Duration.ofMinutes(1)).isEmpty());
        verify(movieRepository).findByTitle("movie");
    }

    @Test
    void testListMoviesShouldReturnTheListOfAddedMovies() {
        Movie movie = new Movie("movie", "genre", Duration.ofMinutes(1));
        when(movieRepository.findAll()).thenReturn(List.of(movie));

        List<MovieDto> expected = List.of(new MovieDto("movie", "genre", Duration.ofMinutes(1)));
        assertEquals(expected, underTest.listMovies());
        verify(movieRepository).findAll();
    }

    @Test
    void testUpdateMovieShouldReturnTheUpdatedMovie() {
        Movie movie = new Movie("movie", "genre", Duration.ofMinutes(1));
        when(movieRepository.findByTitle("movie")).thenReturn(Optional.of(movie));

        MovieDto expected = new MovieDto("movie", "drama", Duration.ofMinutes(10));
        assertEquals(expected, underTest.updateMovie("movie", "drama", Duration.ofMinutes(10)).get());
        verify(movieRepository).findByTitle("movie");
    }

    @Test
    void testUpdateMovieShouldReturnEmptyWhenTheMovieDoesNotExists() {
        when(movieRepository.findByTitle("movie")).thenReturn(Optional.empty());

        assertTrue(underTest.updateMovie("movie", "genre", Duration.ofMinutes(1)).isEmpty());
        verify(movieRepository).findByTitle("movie");
    }

    @Test
    void testDeleteMovieShouldReturnTrueWhenItDeletesAMovie() {
        Movie movie = new Movie("movie", "genre", Duration.ofMinutes(1));
        when(movieRepository.findByTitle("movie")).thenReturn(Optional.of(movie));

        assertTrue(underTest.deleteMovie("movie"));
        verify(movieRepository).findByTitle("movie");
    }

    @Test
    void testDeleteMovieShouldReturnFalseWhenItDidNotDeleteAnything() {
        when(movieRepository.findByTitle(ArgumentMatchers.any())).thenReturn(Optional.empty());

        assertFalse(underTest.deleteMovie("movie"));
        verify(movieRepository).findByTitle("movie");
    }
}