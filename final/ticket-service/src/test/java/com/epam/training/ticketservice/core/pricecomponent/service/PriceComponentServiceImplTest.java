package com.epam.training.ticketservice.core.pricecomponent.service;

import com.epam.training.ticketservice.core.movie.persistence.MovieRepository;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponent;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponentRepository;
import com.epam.training.ticketservice.core.room.persistance.RoomRepository;
import com.epam.training.ticketservice.core.screening.persistence.ScreeningRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PriceComponentServiceImplTest {
    final private RoomRepository roomRepository = mock(RoomRepository.class);
    final private MovieRepository movieRepository = mock(MovieRepository.class);
    final private ScreeningRepository screeningRepository = mock(ScreeningRepository.class);
    final private PriceComponentRepository priceComponentRepository = mock(PriceComponentRepository.class);

    final private PriceComponentServiceImpl underTest =
            new PriceComponentServiceImpl(priceComponentRepository, roomRepository,
                    movieRepository, screeningRepository);

    @Test
    void testCreatePriceComponentShouldCreatePriceComponentWhenParametersAreValid() {
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.empty());

        underTest.createPriceComponent("comp", 100);

        verify(priceComponentRepository).findByName("comp");
    }

    @Test
    void testCreatePriceComponentShouldThrowExceptionWhenCompAlreadyExists() {
        PriceComponent priceComponent = new PriceComponent("comp", 100);
        when(priceComponentRepository.findByName("comp")).thenReturn(Optional.of(priceComponent));


        assertThrows(IllegalArgumentException.class, () ->
                underTest.createPriceComponent("comp", 100));

        verify(priceComponentRepository).findByName("comp");
    }

    @Test
    void testInitShouldExecuteWithoutExceptionsWhenBasePriceCompIsPresent() {
        PriceComponent priceComponent = new PriceComponent("BASE_PRICE_COMPONENT", 1500);
        when(priceComponentRepository.findByName("BASE_PRICE_COMPONENT"))
                .thenReturn(Optional.of(priceComponent));

        underTest.checkBasePriceComp();

        verify(priceComponentRepository).findByName("BASE_PRICE_COMPONENT");
    }

    @Test
    void testInitShouldThrowExceptionWhenBasePriceCompIsEmpty() {
        when(priceComponentRepository.findByName("BASE_PRICE_COMPONENT"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                underTest.checkBasePriceComp());

        verify(priceComponentRepository).findByName("BASE_PRICE_COMPONENT");
    }
}