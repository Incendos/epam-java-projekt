package com.epam.training.ticketservice.core.movie.model;

import java.time.Duration;

public record MovieDto(String title, String genre, Duration length) {
}
