package org.springframework.samples.petclinic.shift;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;

@DataJpaTest
class ShiftRepositoryTests {

    @Autowired
    private ShiftRepository shifts;

    @Autowired
    private VetRepository vets;

    @Test
    void shouldFindShiftsByVetId() {
        List<Shift> foundShifts = shifts.findByVetId(1);
        assertThat(foundShifts).isNotEmpty();
        assertThat(foundShifts.get(0).getVet().getId()).isEqualTo(1);
    }

    @Test
    void shouldFindShiftsByDateRange() {
        LocalDate startDate = LocalDate.of(2024, 3, 18);
        LocalDate endDate = LocalDate.of(2024, 3, 20);
        List<Shift> foundShifts = shifts.findByDateBetweenOrderByDateAscStartTimeAsc(startDate, endDate);
        assertThat(foundShifts).isNotEmpty();
        assertThat(foundShifts.get(0).getDate()).isNotNull();
        assertThat(foundShifts.get(0).getDate().compareTo(startDate)).isGreaterThanOrEqualTo(0);
        assertThat(foundShifts.get(0).getDate().compareTo(endDate)).isLessThanOrEqualTo(0);
    }

    @Test
    void shouldDetectOverlappingShifts() {
        Vet vet = vets.findById(1).orElseThrow();
        LocalDate date = LocalDate.of(2024, 3, 18);
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        boolean hasOverlap = shifts.hasOverlappingShifts(vet, date, startTime, endTime, null);
        assertThat(hasOverlap).isTrue();
    }

    @Test
    void shouldNotDetectOverlappingShiftsForDifferentDates() {
        Vet vet = vets.findById(1).orElseThrow();
        LocalDate date = LocalDate.of(2024, 3, 21); // A date with no shifts
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);

        boolean hasOverlap = shifts.hasOverlappingShifts(vet, date, startTime, endTime, null);
        assertThat(hasOverlap).isFalse();
    }
} 