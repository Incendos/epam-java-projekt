package com.epam.training.ticketservice.core.screening.persistence;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Screening {
    @GeneratedValue
    @Id
    Integer id;

    String movieTitle;
    String roomName;
    LocalDateTime dateTime;

    public Screening(String movieTitle, String roomName, LocalDateTime dateTime) {
        this.movieTitle = movieTitle;
        this.roomName = roomName;
        this.dateTime = dateTime;
    }
}
