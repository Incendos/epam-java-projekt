package com.epam.training.ticketservice.core.pricecomponent.persistence;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class PriceComponent {
    @Id
    @GeneratedValue
    Integer id;

    @Column(unique = true)
    String name;

    Integer price;

    public PriceComponent(String name, Integer price) {
        this.name = name;
        this.price = price;
    }
}
