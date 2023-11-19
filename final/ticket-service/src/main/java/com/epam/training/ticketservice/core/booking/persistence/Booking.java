package com.epam.training.ticketservice.core.booking.persistence;

import com.epam.training.ticketservice.core.screening.persistence.Screening;
import com.epam.training.ticketservice.core.user.persistence.User;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue
    Integer id;

    @ManyToOne
    User user;

    @ManyToOne
    Screening screening;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Seat> seats;

    int price;

    public Booking(User user, Screening screening, List<Seat> seats, int price) {
        this.user = user;
        this.screening = screening;
        this.seats = seats;
        this.price = price;
    }
}