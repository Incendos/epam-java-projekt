package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.service.BookingService;
import com.epam.training.ticketservice.core.movie.service.MovieService;
import com.epam.training.ticketservice.core.room.service.RoomService;
import com.epam.training.ticketservice.core.screening.service.ScreeningService;
import org.jline.reader.ParsedLine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.Input;
import org.springframework.shell.Shell;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("it")
class BookingCommandIT {
    @Autowired
    private Shell shell;

    @Autowired
    private BookingService bookingService;

    @Test
    void testShowPriceForCommandShouldReturnTheCorrectCost() {
        //Given
        shell.evaluate(() -> "sign in privileged admin admin");
        shell.evaluate(() -> "create movie shrek drama 120");
        shell.evaluate(() -> "create room szoba 10 10");
        //Agyhalál
        Input input = new Input() {
            @Override
            public String rawText() {
                return "create screening shrek szoba \"2023-01-01 16:00\"";
            }

            @Override
            public List<String> words() {
                return List.of("create", "screening", "shrek", "szoba", "2023-01-01 16:00");
            }
        };
        shell.evaluate(input);

        LocalDateTime dateTime = LocalDateTime.of(2023,1,1,16,0);
        List<SeatDto> seats = List.of(new SeatDto(1,1));
        assertEquals(1500, bookingService.showPriceFor("shrek", "szoba", dateTime, seats));
    }
}