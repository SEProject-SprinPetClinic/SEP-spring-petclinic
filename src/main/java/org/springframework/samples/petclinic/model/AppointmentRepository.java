package org.springframework.samples.petclinic.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

	List<Appointment> findByVetId(Integer vetId);

	List<Appointment> findByPetId(Integer petId);

	List<Appointment> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

}