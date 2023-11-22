package com.epam.training.ticketservice.core.pricecomponent.service;

import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.movie.persistence.MovieRepository;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponent;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponentRepository;
import com.epam.training.ticketservice.core.room.persistance.Room;
import com.epam.training.ticketservice.core.room.persistance.RoomRepository;
import com.epam.training.ticketservice.core.screening.persistence.Screening;
import com.epam.training.ticketservice.core.screening.persistence.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceComponentServiceImpl implements PriceComponentService {

    private final PriceComponentRepository priceComponentRepository;
    private final RoomRepository roomRepository;
    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;

    @Override
    public void createPriceComponent(String name, Integer price) {
        if (priceComponentRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("A price component with this name already exists");
        }
        PriceComponent priceComponent = new PriceComponent(name, price);
        priceComponentRepository.save(priceComponent);
    }

    @Override
    public void updateBasePrice(Integer price) {
        Optional<PriceComponent> basePrice = priceComponentRepository.findByName("BASE_PRICE_COMPONENT");
        basePrice.get().setPrice(price);
        priceComponentRepository.save(basePrice.get());
    }

    @Override
    public void attachPriceComponentToRoom(String priceComponentName, String roomName) {
        Optional<PriceComponent> priceComponent = priceComponentRepository.findByName(priceComponentName);
        if (priceComponent.isEmpty()) {
            throw new IllegalArgumentException("No such price component exists");
        }

        Optional<Room> room = roomRepository.findByName(roomName);
        if (room.isEmpty()) {
            throw new IllegalArgumentException("No such room exists");
        }

        List<PriceComponent> priceComponents = room.get().getPriceComponents();
        priceComponents.add(priceComponent.get());
        room.get().setPriceComponents(priceComponents);
        roomRepository.save(room.get());
    }

    @Override
    public void attachPriceComponentToMovie(String priceComponentName, String movieTitle) {
        Optional<PriceComponent> priceComponent = priceComponentRepository.findByName(priceComponentName);
        if (priceComponent.isEmpty()) {
            throw new IllegalArgumentException("No such price component exists");
        }

        Optional<Movie> movie = movieRepository.findByTitle(movieTitle);
        if (movie.isEmpty()) {
            throw new IllegalArgumentException("No such movie exists");
        }

        List<PriceComponent> priceComponents = movie.get().getPriceComponents();
        priceComponents.add(priceComponent.get());
        movie.get().setPriceComponents(priceComponents);
        movieRepository.save(movie.get());
    }

    @Override
    public void attachPriceComponentToScreening(String priceComponentName, String movieTitle, String roomName,
                                                LocalDateTime dateTime) {
        Optional<PriceComponent> priceComponent = priceComponentRepository.findByName(priceComponentName);
        if (priceComponent.isEmpty()) {
            throw new IllegalArgumentException("No such price component exists");
        }

        Optional<Screening> screening = screeningRepository.findByMovieTitleAndRoomNameAndDateTime(movieTitle,
                roomName, dateTime);
        if (screening.isEmpty()) {
            throw new IllegalArgumentException("No such screening exists");
        }

        List<PriceComponent> priceComponents = screening.get().getPriceComponents();
        priceComponents.add(priceComponent.get());
        screening.get().setPriceComponents(priceComponents);
        screeningRepository.save(screening.get());
    }
}
