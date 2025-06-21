package org.springframework.samples.petclinic.shift;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository class for <code>Shift</code> domain objects
 */
@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {

	/**
	 * Find shifts for a specific vet
	 * @param vet the vet to find shifts for
	 * @return a Collection of shifts
	 */
	Page<Shift> findByVetOrderByDateAscStartTimeAsc(Vet vet, Pageable pageable);

	/**
	 * Find shifts for a specific date
	 * @param date the date to find shifts for
	 * @return a Collection of shifts
	 */
	Page<Shift> findByDateOrderByStartTimeAsc(LocalDate date, Pageable pageable);

	/**
	 * Find all shifts ordered by date and time
	 * @return a Collection of all shifts
	 */
	Page<Shift> findAllByOrderByDateAscStartTimeAsc(Pageable pageable);

	/**
	 * Check if there are any overlapping shifts for a vet on a specific date
	 * @param vet the vet to check shifts for
	 * @param date the date to check
	 * @param startTime the start time of the new shift
	 * @param endTime the end time of the new shift
	 * @param shiftId the id of the current shift (can be null for new shifts)
	 * @return true if there are overlapping shifts
	 */
	@Query("SELECT COUNT(s) > 0 FROM Shift s WHERE s.vet = :vet AND s.date = :date "
			+ "AND s.id != COALESCE(:shiftId, -1) " + "AND ((s.startTime <= :startTime AND s.endTime > :startTime) "
			+ "OR (s.startTime < :endTime AND s.endTime >= :endTime) "
			+ "OR (s.startTime >= :startTime AND s.endTime <= :endTime))")
	boolean hasOverlappingShifts(@Param("vet") Vet vet, @Param("date") LocalDate date,
			@Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime,
			@Param("shiftId") Integer shiftId);

	/**
	 * Find shifts for a specific vet
	 */
	List<Shift> findByVetId(Integer vetId);

	/**
	 * Find shifts for a date range
	 */
	List<Shift> findByDateBetweenOrderByDateAscStartTimeAsc(LocalDate startDate, LocalDate endDate);

	/**
	 * Find shifts for a specific vet in a date range
	 */
	List<Shift> findByVetIdAndDateBetweenOrderByDateAscStartTimeAsc(Integer vetId, LocalDate startDate,
			LocalDate endDate);

	/**
	 * Check for overlapping shifts for a vet on a specific date
	 */
	@Query("SELECT COUNT(s) > 0 FROM Shift s WHERE s.vet.id = :vetId " + "AND s.date = :date "
			+ "AND ((s.startTime <= :endTime AND s.endTime >= :startTime) "
			+ "OR (s.startTime >= :startTime AND s.startTime < :endTime))")
	boolean hasOverlappingShift(@Param("vetId") Integer vetId, @Param("date") LocalDate date,
			@Param("startTime") java.time.LocalTime startTime, @Param("endTime") java.time.LocalTime endTime);

}