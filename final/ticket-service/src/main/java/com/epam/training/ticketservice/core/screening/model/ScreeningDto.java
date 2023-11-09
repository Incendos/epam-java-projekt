package com.epam.training.ticketservice.core.screening.model;

import java.time.LocalDateTime;

public record ScreeningDto(String movieTitle, String roomName, LocalDateTime dateTime) {
}
