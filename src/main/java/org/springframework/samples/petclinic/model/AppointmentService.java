package org.springframework.samples.petclinic.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.owner.PetRepository;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

	private final AppointmentRepository appointmentRepository;

	private final VetRepository vetRepository;

	private final PetRepository petRepository;

	@Autowired
	public AppointmentService(AppointmentRepository appointmentRepository, VetRepository vetRepository,
			PetRepository petRepository) {
		this.appointmentRepository = appointmentRepository;
		this.vetRepository = vetRepository;
		this.petRepository = petRepository;
	}

	public Appointment bookAppointment(Integer petId, Integer vetId, LocalDateTime dateTime, String type) {
		if (!isSlotAvailable(vetId, dateTime)) {
			throw new IllegalStateException("Appointment slot not available");
		}
		Appointment appointment = new Appointment();
		appointment.setPet(petRepository.findById(petId).orElseThrow());
		appointment.setVet(vetRepository.findById(vetId).orElseThrow());
		appointment.setDateTime(dateTime);
		appointment.setType(type);
		appointment.setStatus("SCHEDULED");
		return appointmentRepository.save(appointment);
	}

	public void cancelAppointment(Integer appointmentId) {
		Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
		appointment.setStatus("CANCELLED");
		appointmentRepository.save(appointment);
	}

	public void rescheduleAppointment(Integer appointmentId, LocalDateTime newDateTime) {
		Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
		if (!isSlotAvailable(appointment.getVet().getId(), newDateTime)) {
			throw new IllegalStateException("Appointment slot not available");
		}
		appointment.setDateTime(newDateTime);
		appointment.setStatus("RESCHEDULED");
		appointmentRepository.save(appointment);
	}

	public boolean isSlotAvailable(Integer vetId, LocalDateTime dateTime) {
		List<Appointment> appointments = appointmentRepository.findByVetId(vetId);
		return appointments.stream()
			.noneMatch(a -> a.getDateTime().equals(dateTime) && "SCHEDULED".equals(a.getStatus()));
	}

	public List<Appointment> getAppointmentsForVet(Integer vetId) {
		return appointmentRepository.findByVetId(vetId);
	}

	public List<Appointment> getAppointmentsForPet(Integer petId) {
		return appointmentRepository.findByPetId(petId);
	}

	public Optional<Appointment> getAppointmentById(Integer appointmentId) {
		return appointmentRepository.findById(appointmentId);
	}

	public List<Appointment> getAllAppointments() {
		return appointmentRepository.findAll();
	}

}