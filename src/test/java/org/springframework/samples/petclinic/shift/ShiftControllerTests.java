package org.springframework.samples.petclinic.shift;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(ShiftController.class)
class ShiftControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ShiftRepository shifts;

	@MockBean
	private VetRepository vets;

	private Shift testShift;
	private List<Shift> testShifts;
	private Vet testVet;

	@BeforeEach
	void setup() {
		testVet = new Vet();
		testVet.setId(1);
		testVet.setFirstName("James");
		testVet.setLastName("Carter");

		testShift = new Shift();
		testShift.setId(1);
		testShift.setVet(testVet);
		testShift.setDate(LocalDate.now());
		testShift.setStartTime(LocalTime.of(9, 0));
		testShift.setEndTime(LocalTime.of(17, 0));

		testShifts = new ArrayList<>();
		testShifts.add(testShift);

		Page<Vet> vetPage = new PageImpl<>(List.of(testVet));
		given(vets.findAll(PageRequest.of(0, 100))).willReturn(vetPage);
		given(shifts.findById(1)).willReturn(Optional.of(testShift));
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/shifts/new"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("shift"))
			.andExpect(view().name("shifts/createOrUpdateShiftForm"));
	}

	@Test
	void testListShifts() throws Exception {
		given(shifts.findByDateBetweenOrderByDateAscStartTimeAsc(
				LocalDate.now().with(java.time.temporal.WeekFields.of(java.util.Locale.getDefault()).dayOfWeek(), 1),
				LocalDate.now()
					.with(java.time.temporal.WeekFields.of(java.util.Locale.getDefault()).dayOfWeek(), 1)
					.plusDays(6)))
			.willReturn(testShifts);

		mockMvc.perform(get("/shifts"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("shifts"))
			.andExpect(view().name("shifts/shiftList"));
	}

	@Test
	void testInitUpdateForm() throws Exception {
		mockMvc.perform(get("/shifts/1/edit"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("shift"))
			.andExpect(view().name("shifts/createOrUpdateShiftForm"));
	}

	@Test
	void testProcessUpdateFormSuccess() throws Exception {
		mockMvc.perform(post("/shifts/1/edit")
				.param("date", LocalDate.now().toString())
				.param("startTime", "09:00")
				.param("endTime", "17:00")
				.param("vet.id", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/shifts"));
	}
}