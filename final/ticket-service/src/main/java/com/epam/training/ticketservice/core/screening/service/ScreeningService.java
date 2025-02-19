package com.epam.training.ticketservice.core.screening.service;

import com.epam.training.ticketservice.core.screening.model.ScreeningDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScreeningService {
    Optional<ScreeningDto> createScreening(String movieTitle, String roomName, LocalDateTime dateTime);

    boolean deleteScreening(String movieTitle, String roomName, LocalDateTime dateTime);

    List<ScreeningDto> listScreenings();
}
