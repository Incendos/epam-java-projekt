package com.epam.training.ticketservice.core.room.persistance;

import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponent;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Room {
    @GeneratedValue
    @Id
    Integer id;

    @Column(unique = true)
    String name;

    Integer rowCount;
    Integer columnCount;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany
    List<PriceComponent> priceComponents;

    public Room(String name, Integer rowCount, Integer columnCount) {
        this.name = name;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.priceComponents = List.of();
    }
}
