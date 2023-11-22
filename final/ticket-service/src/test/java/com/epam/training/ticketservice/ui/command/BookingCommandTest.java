package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.Input;
import org.springframework.shell.Shell;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("it")
class BookingCommandTest {
    @Autowired
    private Shell shell;

    @Autowired
    private BookingService bookingService;

    @Test
    void testBookCommandShouldLetTheSignInUserBook() {
        shell.evaluate(() -> "sign in privileged admin admin");
        shell.evaluate(() -> "create movie shrek drama 120");
        shell.evaluate(() -> "create room szoba 10 10");

        Input createScreeningInput = new Input() {
            @Override
            public String rawText() {
                return "create screening shrek szoba \"2023-01-01 16:00\"";
            }

            @Override
            public List<String> words() {
                return List.of("create", "screening", "shrek", "szoba", "2023-01-01 16:00");
            }
        };
        shell.evaluate(createScreeningInput);

        shell.evaluate(() -> "sign up user user");
        shell.evaluate(() -> "sign in user user");
        Input bookingInput = new Input() {
            @Override
            public String rawText() {
                return "book shrek szoba \"2023-01-01 16:00\" \"1,1\"";
            }

            @Override
            public List<String> words() {
                return List.of("book", "shrek", "szoba", "2023-01-01 16:00", "1,1");
            }
        };

        assertEquals("Seats booked: (1,1); the price for this booking is 1500 HUF",
                shell.evaluate(bookingInput));
    }
}