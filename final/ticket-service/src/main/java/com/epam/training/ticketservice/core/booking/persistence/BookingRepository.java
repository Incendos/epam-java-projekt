package com.epam.training.ticketservice.core.booking.persistence;

import com.epam.training.ticketservice.core.screening.persistence.Screening;
import com.epam.training.ticketservice.core.user.persistence.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    public List<Booking> findAllByScreening(Screening screening);

    public List<Booking> findAllByUser(User user);
}
