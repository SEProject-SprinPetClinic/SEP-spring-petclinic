package org.springframework.samples.petclinic.shift;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.vet.Vet;

class ShiftTests {

    @Test
    void shouldHaveCorrectProperties() {
        Shift shift = new Shift();
        LocalDate date = LocalDate.now();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        Vet vet = new Vet();
        vet.setId(1);

        shift.setDate(date);
        shift.setStartTime(startTime);
        shift.setEndTime(endTime);
        shift.setVet(vet);

        assertThat(shift.getDate()).isEqualTo(date);
        assertThat(shift.getStartTime()).isEqualTo(startTime);
        assertThat(shift.getEndTime()).isEqualTo(endTime);
        assertThat(shift.getVet()).isEqualTo(vet);
    }
} 