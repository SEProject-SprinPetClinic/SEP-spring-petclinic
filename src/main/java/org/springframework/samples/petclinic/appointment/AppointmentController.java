package org.springframework.samples.petclinic.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.Appointment;
import org.springframework.samples.petclinic.model.AppointmentService;
import org.springframework.samples.petclinic.owner.PetRepository;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

	private final AppointmentService appointmentService;

	private final PetRepository petRepository;

	private final VetRepository vetRepository;

	@Autowired
	public AppointmentController(AppointmentService appointmentService, PetRepository petRepository,
			VetRepository vetRepository) {
		this.appointmentService = appointmentService;
		this.petRepository = petRepository;
		this.vetRepository = vetRepository;
	}

	// Show form to book a new appointment
	@GetMapping("/new")
	public String initNewAppointmentForm(Model model) {
		model.addAttribute("appointment", new Appointment());
		model.addAttribute("pets", petRepository.findAll());
		model.addAttribute("vets", vetRepository.findAll());
		return "appointments/createOrUpdateAppointmentForm";
	}

	// Process booking a new appointment
	@PostMapping("/new")
	public String processNewAppointment(@RequestParam Integer petId, @RequestParam Integer vetId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
			@RequestParam String type, Model model) {
		try {
			appointmentService.bookAppointment(petId, vetId, dateTime, type);
			return "redirect:/appointments/confirmation";
		}
		catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("appointment", new Appointment());
			model.addAttribute("pets", petRepository.findAll());
			model.addAttribute("vets", vetRepository.findAll());
			return "appointments/createOrUpdateAppointmentForm";
		}
	}

	// List all appointments for a vet (calendar view)
	@GetMapping("/vet/{vetId}")
	public String listAppointmentsForVet(@PathVariable Integer vetId, Model model) {
		List<Appointment> appointments = appointmentService.getAppointmentsForVet(vetId);
		model.addAttribute("appointments", appointments);
		return "appointments/vetAppointments";
	}

	// List all appointments for a pet (owner view)
	@GetMapping("/pet/{petId}")
	public String listAppointmentsForPet(@PathVariable Integer petId, Model model) {
		List<Appointment> appointments = appointmentService.getAppointmentsForPet(petId);
		model.addAttribute("appointments", appointments);
		return "appointments/petAppointments";
	}

	// Cancel an appointment
	@PostMapping("/cancel/{appointmentId}")
	public String cancelAppointment(@PathVariable Integer appointmentId) {
		appointmentService.cancelAppointment(appointmentId);
		return "redirect:/appointments";
	}

	// Reschedule an appointment
	@PostMapping("/reschedule/{appointmentId}")
	public String rescheduleAppointment(@PathVariable Integer appointmentId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDateTime) {
		appointmentService.rescheduleAppointment(appointmentId, newDateTime);
		return "redirect:/appointments";
	}

	// List all appointments (admin or overview)
	@GetMapping
	public String listAllAppointments(Model model) {
		model.addAttribute("appointments", appointmentService.getAllAppointments());
		return "appointments/allAppointments";
	}

	// Show confirmation page after successful booking
	@GetMapping("/confirmation")
	public String showConfirmation() {
		return "appointments/confirmation";
	}

}