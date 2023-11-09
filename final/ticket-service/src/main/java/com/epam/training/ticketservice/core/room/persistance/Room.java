package com.epam.training.ticketservice.core.room.persistance;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

    public Room(String name, Integer rowCount, Integer columnCount) {
        this.name = name;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
    }
}
