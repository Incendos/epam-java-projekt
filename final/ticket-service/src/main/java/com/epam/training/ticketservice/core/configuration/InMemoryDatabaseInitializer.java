package com.epam.training.ticketservice.core.configuration;

import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponent;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponentRepository;
import com.epam.training.ticketservice.core.user.persistence.User;
import com.epam.training.ticketservice.core.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Profile("!default")
public class InMemoryDatabaseInitializer {

    private final UserRepository userRepository;
    private final PriceComponentRepository priceComponentRepository;

    @PostConstruct
    public void init() {
        User admin = new User("admin", "admin", User.Role.ADMIN);
        userRepository.save(admin);

        PriceComponent basePriceComponent = new PriceComponent("BASE_PRICE_COMPONENT", 1500);
        priceComponentRepository.save(basePriceComponent);
    }
}
