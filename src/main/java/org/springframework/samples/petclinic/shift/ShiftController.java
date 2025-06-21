package org.springframework.samples.petclinic.shift;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * Controller for managing veterinarian shifts.
 */
@Controller
@RequestMapping("/shifts")
public class ShiftController {

	private final ShiftRepository shifts;

	private final VetRepository vets;

	public ShiftController(ShiftRepository shifts, VetRepository vets) {
		this.shifts = shifts;
		this.vets = vets;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("vets")
	public Page<org.springframework.samples.petclinic.vet.Vet> populateVets() {
		return this.vets.findAll(PageRequest.of(0, 100));
	}

	@GetMapping()
	public String listShifts(@RequestParam(defaultValue = "0") int page, @RequestParam(required = false) Integer vetId,
			@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate,
			Model model) {

		Pageable pageable = PageRequest.of(page, 20);
		Page<Shift> shiftsPage;

		if (startDate == null) {
			// Default to current week
			LocalDate now = LocalDate.now();
			WeekFields weekFields = WeekFields.of(Locale.getDefault());
			startDate = now.with(weekFields.dayOfWeek(), 1);
			endDate = startDate.plusDays(6);
		}

		if (endDate == null) {
			endDate = startDate.plusDays(6);
		}

		if (vetId != null) {
			model.addAttribute("shifts",
					shifts.findByVetIdAndDateBetweenOrderByDateAscStartTimeAsc(vetId, startDate, endDate));
		}
		else {
			model.addAttribute("shifts", shifts.findByDateBetweenOrderByDateAscStartTimeAsc(startDate, endDate));
		}

		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("selectedVetId", vetId);

		return "shifts/shiftList";
	}

	@GetMapping("/new")
	public String initCreationForm(Map<String, Object> model) {
		Shift shift = new Shift();
		shift.setDate(LocalDate.now());
		model.put("shift", shift);
		return "shifts/createOrUpdateShiftForm";
	}

	@PostMapping("/new")
	public String processCreationForm(@Valid Shift shift, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "shifts/createOrUpdateShiftForm";
		}

		if (shift.getStartTime().isAfter(shift.getEndTime())) {
			result.rejectValue("endTime", "timeSequence", "End time must be after start time");
			return "shifts/createOrUpdateShiftForm";
		}

		if (shifts.hasOverlappingShift(shift.getVet().getId(), shift.getDate(), shift.getStartTime(),
				shift.getEndTime())) {
			result.rejectValue("startTime", "duplicate",
					"This vet already has a shift that overlaps with this time period");
			return "shifts/createOrUpdateShiftForm";
		}

		shifts.save(shift);
		redirectAttributes.addFlashAttribute("message", "Shift created successfully");
		return "redirect:/shifts";
	}

	@GetMapping("/{shiftId}/edit")
	public String initUpdateForm(@PathVariable("shiftId") int shiftId, Model model) {
		Shift shift = shifts.findById(shiftId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid shift Id:" + shiftId));
		model.addAttribute(shift);
		return "shifts/createOrUpdateShiftForm";
	}

	@PostMapping("/{shiftId}/edit")
	public String processUpdateForm(@Valid Shift shift, BindingResult result, @PathVariable("shiftId") int shiftId,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "shifts/createOrUpdateShiftForm";
		}

		if (shift.getStartTime().isAfter(shift.getEndTime())) {
			result.rejectValue("endTime", "timeSequence", "End time must be after start time");
			return "shifts/createOrUpdateShiftForm";
		}

		shift.setId(shiftId);
		shifts.save(shift);
		redirectAttributes.addFlashAttribute("message", "Shift updated successfully");
		return "redirect:/shifts";
	}

	@GetMapping("/{shiftId}/delete")
	public String deleteShift(@PathVariable("shiftId") int shiftId, RedirectAttributes redirectAttributes) {
		shifts.deleteById(shiftId);
		redirectAttributes.addFlashAttribute("message", "Shift deleted successfully");
		return "redirect:/shifts";
	}

}