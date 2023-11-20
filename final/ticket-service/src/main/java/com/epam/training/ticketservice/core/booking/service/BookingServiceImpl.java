package com.epam.training.ticketservice.core.booking.service;

import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.persistence.Booking;
import com.epam.training.ticketservice.core.booking.persistence.BookingRepository;
import com.epam.training.ticketservice.core.booking.persistence.Seat;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponent;
import com.epam.training.ticketservice.core.pricecomponent.persistence.PriceComponentRepository;
import com.epam.training.ticketservice.core.room.persistance.Room;
import com.epam.training.ticketservice.core.room.persistance.RoomRepository;
import com.epam.training.ticketservice.core.screening.persistence.Screening;
import com.epam.training.ticketservice.core.screening.persistence.ScreeningRepository;
import com.epam.training.ticketservice.core.user.persistence.User;
import com.epam.training.ticketservice.core.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ScreeningRepository screeningRepository;
    private final UserRepository userRepository;

    private final PriceComponentRepository priceComponentRepository;

    @Override
    public BookingDto book(String userName, String movieTitle, String roomName,
                           LocalDateTime dateTime, List<SeatDto> seats) {
        Optional<Screening> screening = screeningRepository
                .findByMovieTitleAndRoomNameAndDateTime(movieTitle, roomName, dateTime);

        if (screening.isEmpty()) {
            throw new IllegalArgumentException("No such screening exists");
        }

        Room room = screening.get().getRoom();
        for (SeatDto seat : seats) {
            if (room.getRowCount() < seat.row() || seat.row() < 1
                || room.getColumnCount() < seat.column() || seat.column() < 1) {
                throw new IllegalArgumentException("Seat " + seat + " does not exist in this room");
            }
        }

        bookingRepository.findAllByScreening(screening.get()).stream()
                .map((booking) -> booking.getSeats().stream().map(SeatDto::new).toList())
                .flatMap(Collection::stream)
                .forEach((occupiedSeat) -> {
                    if (seats.contains(occupiedSeat)) {
                        throw new IllegalArgumentException("Seat " + occupiedSeat + " is already taken");
                    }
                });


        Optional<User> user = userRepository.findByUsername(userName);
        if (user.isEmpty() || user.get().getRole() == User.Role.ADMIN) {
            throw new IllegalArgumentException("Invalid User: log in as not an admin");
        }

        int totalPrice = priceComponentRepository.findByName("BASE_PRICE_COMPONENT").get().getPrice();
        for (PriceComponent priceComponent : room.getPriceComponents()) {
            totalPrice += priceComponent.getPrice();
        }
        for (PriceComponent priceComponent : screening.get().getMovie().getPriceComponents()) {
            totalPrice += priceComponent.getPrice();
        }
        for (PriceComponent priceComponent : screening.get().getPriceComponents()) {
            totalPrice += priceComponent.getPrice();
        }

        Booking booking = new Booking(user.get(), screening.get(), seats.stream().map(Seat::new).toList(),
                totalPrice * seats.size());
        bookingRepository.save(booking);
        return new BookingDto(booking);
    }

    @Override
    public List<BookingDto> listBookings(String userName) {
        Optional<User> user = userRepository.findByUsername(userName);
        if (user.isEmpty() || user.get().getRole() == User.Role.ADMIN) {
            throw new IllegalArgumentException("Invalid user: log in as not an admin");
        }
        return bookingRepository.findAllByUser(user.get()).stream().map(BookingDto::new).toList();
    }



    @Override
    public Integer showPriceFor(String movieTitle, String roomName, LocalDateTime dateTime, List<SeatDto> seats) {
        Optional<Screening> screening = screeningRepository
                .findByMovieTitleAndRoomNameAndDateTime(movieTitle, roomName, dateTime);

        if (screening.isEmpty()) {
            throw new IllegalArgumentException("No such screening exists");
        }

        Room room = screening.get().getRoom();
        for (SeatDto seat : seats) {
            if (room.getRowCount() < seat.row() || seat.row() < 1
                    || room.getColumnCount() < seat.column() || seat.column() < 1) {
                throw new IllegalArgumentException("Seat " + seat + " does not exist in this room");
            }
        }

        int totalPrice = priceComponentRepository.findByName("BASE_PRICE_COMPONENT").get().getPrice();
        for (PriceComponent priceComponent : room.getPriceComponents()) {
            totalPrice += priceComponent.getPrice();
        }
        for (PriceComponent priceComponent : screening.get().getMovie().getPriceComponents()) {
            totalPrice += priceComponent.getPrice();
        }
        for (PriceComponent priceComponent : screening.get().getPriceComponents()) {
            totalPrice += priceComponent.getPrice();
        }
        return totalPrice * seats.size();
    }
}
