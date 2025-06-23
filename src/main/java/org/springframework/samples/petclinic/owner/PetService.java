
package org.springframework.samples.petclinic.owner;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class PetService {

	private final PetRepository petRepository;

	private final VisitRepository visitRepository;

	@Autowired
	public PetService(PetRepository petRepository, VisitRepository visitRepository) {
		this.petRepository = petRepository;
		this.visitRepository = visitRepository;
	}

	public String getBadgeForPet(int petId) {
		int visitCount = visitRepository.countByPetId(petId);

		if (visitCount == 1) {
			return "New";
		}
		else if (visitCount >= 3) {
			return "Frequent Visitor";
		}
		else {
			return "";
		}
	}

}
