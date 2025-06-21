package org.springframework.samples.petclinic.shift;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql({"/schema.sql", "/data.sql"})
class ShiftRepositoryTests {

	@Autowired
	private ShiftRepository shifts;

	@Autowired
	private VetRepository vets;

	private Shift testShift;
	private Vet testVet;

	@BeforeEach
	void setup() {
		testVet = vets.findById(1).orElseThrow();
		testShift = new Shift();
		testShift.setVet(testVet);
		testShift.setDate(LocalDate.of(2024, 3, 18));
		testShift.setStartTime(LocalTime.of(9, 0));
		testShift.setEndTime(LocalTime.of(17, 0));
		shifts.save(testShift);
	}

	@Test
	void shouldFindShiftsByVetId() {
		List<Shift> foundShifts = shifts.findByVetId(testVet.getId());
		assertThat(foundShifts).isNotEmpty();
		assertThat(foundShifts.get(0).getVet().getId()).isEqualTo(testVet.getId());
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
		LocalTime startTime = LocalTime.of(8, 0);
		LocalTime endTime = LocalTime.of(10, 0);

		boolean hasOverlap = shifts.hasOverlappingShifts(testVet, testShift.getDate(), startTime, endTime, null);
		assertThat(hasOverlap).isTrue();
	}

	@Test
	void shouldNotDetectOverlappingShiftsForDifferentDates() {
		LocalDate differentDate = testShift.getDate().plusDays(1);
		LocalTime startTime = LocalTime.of(9, 0);
		LocalTime endTime = LocalTime.of(17, 0);

		boolean hasOverlap = shifts.hasOverlappingShifts(testVet, differentDate, startTime, endTime, null);
		assertThat(hasOverlap).isFalse();
	}
}