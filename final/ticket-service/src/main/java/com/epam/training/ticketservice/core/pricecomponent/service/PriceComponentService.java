package com.epam.training.ticketservice.core.pricecomponent.service;

import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponent;
import com.epam.training.ticketservice.core.room.persistance.Room;

import java.time.LocalDateTime;

public interface PriceComponentService {
    public void createPriceComponent(String name, Integer price);

    public void updateBasePrice(Integer price);

    public void  attachPriceComponentToRoom(String priceComponentName, String roomName);

    public void attachPriceComponentToMovie(String priceComponentName, String movieTitle);

    public void attachPriceComponentToScreening(String priceComponentName, String movieTitle, String roomName,
                                                LocalDateTime dateTime);
}
