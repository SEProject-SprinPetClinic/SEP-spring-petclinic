/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 * @author Wick Dynex
 */
@Controller
public class VisitController {

	private final OwnerRepository owners;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private SMSService smsService;

	public VisitController(OwnerRepository owners) {
		this.owners = owners;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	/**
	 * Called before each and every @RequestMapping annotated method. 2 goals: - Make sure
	 * we always have fresh data - Since we do not use the session scope, make sure that
	 * Pet object always has an id (Even though id is not part of the form fields)
	 * @param petId
	 * @return Pet
	 */
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId,
			Map<String, Object> model) {
		Optional<Owner> optionalOwner = owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));

		Pet pet = owner.getPet(petId);
		model.put("pet", pet);
		model.put("owner", owner);

		Visit visit = new Visit();
		pet.addVisit(visit);
		return visit;
	}

	// Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is
	// called
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String initNewVisitForm() {
		return "pets/createOrUpdateVisitForm";
	}

	// Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is
	// called
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@ModelAttribute Owner owner, @PathVariable int petId, @Valid Visit visit,
			BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}

		owner.addVisit(petId, visit);
		this.owners.save(owner);

		// SMS gönderimi: Eğer randevu tam 24 saat sonraysa (saat bazında,
		// dakika/saniye/milisaniye önemsiz)
		if (visit.getDate() != null && visit.getTime() != null) {
			java.time.LocalDateTime now = java.time.LocalDateTime.now();
			java.time.LocalDateTime visitDateTime = java.time.LocalDateTime.of(visit.getDate(), visit.getTime());
			java.time.Duration duration = java.time.Duration.between(now, visitDateTime);
			if (duration.toHours() == 24) {
				String ownerName = owner.getFirstName() + " " + owner.getLastName();
				String petName = visit.getPet() != null ? visit.getPet().getName() : "";
				String phone = owner.getTelephone();
				String msg = "Dear " + ownerName + ", you have an appointment for your pet " + petName + " on "
						+ visit.getDate() + " at " + visit.getTime() + ".";
				try {
					smsService.sendSMS(phone, msg);
				}
				catch (Exception e) {
					// Hata loglanabilir veya kullanıcıya gösterilebilir
				}
			}
		}

		if (visit.isFutureAppointment()) {
			redirectAttributes.addFlashAttribute("message",
					(visit.getPet() != null ? visit.getPet().getName() : "") + " appointment has been booked for "
							+ visit.getDate().toString() + " "
							+ (visit.getTime() != null ? visit.getTime().toString() : "") + ".");
		}
		else {
			redirectAttributes.addFlashAttribute("message", "Visit has been booked");
		}
		return "redirect:/owners/{ownerId}";
	}

	@GetMapping("/test-notification")
	@ResponseBody
	public String testNotification() {
		notificationService.sendUpcomingVisitNotifications();
		return "Notification test triggered!";
	}

}
