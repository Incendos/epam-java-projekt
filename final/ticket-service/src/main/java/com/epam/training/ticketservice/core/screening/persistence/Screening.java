package com.epam.training.ticketservice.core.screening.persistence;

import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponent;
import com.epam.training.ticketservice.core.room.persistance.Room;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Screening {
    @GeneratedValue
    @Id
    Integer id;

    @ManyToOne
    Movie movie;

    @ManyToOne
    Room room;

    LocalDateTime dateTime;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany
    List<PriceComponent> priceComponents;

    public Screening(Movie movie, Room room, LocalDateTime dateTime) {
        this.movie = movie;
        this.room = room;
        this.dateTime = dateTime;
        this.priceComponents = new ArrayList<>();
    }
}
