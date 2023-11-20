package com.epam.training.ticketservice.core.pricecomponent.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Repository
public interface PriceComponentRepository extends JpaRepository<PriceComponent, Integer> {

    public Optional<PriceComponent> findByName(String name);

}
