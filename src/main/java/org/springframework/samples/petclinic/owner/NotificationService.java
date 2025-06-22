package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class NotificationService {

	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

	@Autowired
	private VisitRepository visitRepository;

	@Autowired
	private SMSService smsService;

	@Value("${clinic.name}")
	private String clinicName;

	@Value("${clinic.contact}")
	private String clinicContact;

	// 24 saat sonraki randevulara SMS gönder
	public void sendUpcomingVisitNotifications() {
		logger.info("TEST LOG: NotificationService log denemesi");
		System.out.println("TEST: Konsola yazı geliyor mu?");
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		List<Visit> visits = visitRepository.findByDate(tomorrow);
		for (Visit visit : visits) {
			if (visit.getTime() != null && visit.isFutureAppointment()) {
				String phone = visit.getPet().getOwner().getTelephone();
				String message = "Reminder: You have an appointment for " + visit.getPet().getName() + " on "
						+ visit.getDate() + " at " + visit.getTime() + ".\n" + "Clinic: " + clinicName + " | Contact: "
						+ clinicContact;
				try {
					smsService.sendSMS(phone, message);
					logger.info("SMS sent to {} for pet '{}' at {} {}", phone, visit.getPet().getName(),
							visit.getDate(), visit.getTime());
				}
				catch (Exception e) {
					logger.error("Failed to send SMS to {} for pet '{}': {}", phone, visit.getPet().getName(),
							e.getMessage(), e);
				}
			}
		}
	}

}
