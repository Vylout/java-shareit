package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @SneakyThrows
    @Test
    void testItemDto() {
        BookingDto bookingDto = BookingDto.builder().itemId(1L)
                .start(null)
                .end(null)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(null);
    }
}
