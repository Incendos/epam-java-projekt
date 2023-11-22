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
        String errorString = checkScreeningDetails(movieTitle, roomName, dateTime, seats);
        if (!errorString.isEmpty()) {
            throw new IllegalArgumentException(errorString);
        }

        Screening screening = screeningRepository.findByMovieTitleAndRoomNameAndDateTime(movieTitle,
                roomName, dateTime).get();

        bookingRepository.findAllByScreening(screening).stream()
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

        int totalPrice = calcPriceFor(screening, seats.size());

        Booking booking = new Booking(user.get(), screening, seats.stream().map(Seat::new).toList(),
                totalPrice);
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
        String errorString = checkScreeningDetails(movieTitle, roomName, dateTime, seats);
        if (errorString.isEmpty()) {
            return calcPriceFor(
                    screeningRepository.findByMovieTitleAndRoomNameAndDateTime(movieTitle, roomName, dateTime).get(),
                    seats.size());
        }
        throw new IllegalArgumentException(errorString);
    }

    public int calcPriceFor(Screening screening, int numOfSeats) {
        int totalPrice = priceComponentRepository.findByName("BASE_PRICE_COMPONENT").get().getPrice();
        for (PriceComponent priceComponent : screening.getRoom().getPriceComponents()) {
            totalPrice += priceComponent.getPrice();
        }
        for (PriceComponent priceComponent : screening.getMovie().getPriceComponents()) {
            totalPrice += priceComponent.getPrice();
        }
        for (PriceComponent priceComponent : screening.getPriceComponents()) {
            totalPrice += priceComponent.getPrice();
        }
        return totalPrice * numOfSeats;
    }

    public String checkScreeningDetails(String movieTitle, String roomName,
                                         LocalDateTime dateTime, List<SeatDto> seats) {

        Optional<Screening> screening = screeningRepository
                .findByMovieTitleAndRoomNameAndDateTime(movieTitle, roomName, dateTime);

        if (screening.isEmpty()) {
            return "No such screening exists";
        }

        Room room = screening.get().getRoom();
        for (SeatDto seat : seats) {
            if (room.getRowCount() < seat.row() || seat.row() < 1
                    || room.getColumnCount() < seat.column() || seat.column() < 1) {
                return "Seat " + seat + " does not exist in this room";
            }
        }

        return "";
    }
}
