package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class NotificationService {

	@Autowired
	private VisitRepository visitRepository;

	@Autowired
	private SMSService smsService;

	// 24 saat sonraki randevulara SMS gönder
	public void sendUpcomingVisitNotifications() {
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		List<Visit> visits = visitRepository.findByDate(tomorrow);
		for (Visit visit : visits) {
			if (visit.getTime() != null && visit.isFutureAppointment()) {
				String phone = visit.getPet().getOwner().getTelephone();
				String message = "Reminder: You have an appointment for " + visit.getPet().getName() + " on "
						+ visit.getDate() + " at " + visit.getTime() + ".";
				smsService.sendSMS(phone, message);
			}
		}
	}

}