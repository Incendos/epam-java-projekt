package com.epam.training.ticketservice.core.movie.persistence;

import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponent;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.time.Duration;
import java.util.List;


@Entity
@Table(name = "Movies")
@Data
@NoArgsConstructor
public class Movie {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String title;

    private String genre;

    private Duration length;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany
    List<PriceComponent> priceComponents;

    public Movie(String title, String genre, Duration length) {
        this.title = title;
        this.genre = genre;
        this.length = length;
    }
}
