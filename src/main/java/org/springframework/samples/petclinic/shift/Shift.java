package org.springframework.samples.petclinic.shift;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.vet.Vet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Simple JavaBean domain object representing a veterinarian's shift.
 */
@Entity
@Table(name = "shifts")
public class Shift extends BaseEntity {

	@Column(name = "shift_date")
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@Column(name = "start_time")
	@NotNull
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime startTime;

	@Column(name = "end_time")
	@NotNull
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime endTime;

	@ManyToOne
	@JoinColumn(name = "vet_id")
	@NotNull
	private Vet vet;

	public LocalDate getDate() {
		return this.date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getStartTime() {
		return this.startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return this.endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public Vet getVet() {
		return this.vet;
	}

	public void setVet(Vet vet) {
		this.vet = vet;
	}

}