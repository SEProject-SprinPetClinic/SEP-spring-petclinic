package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

	@Autowired
	private NotificationService notificationService;

	// Her gün saat 09:00'da çalışır
	@Scheduled(cron = "0 0 9 * * *")
	public void sendDailyNotifications() {
		notificationService.sendUpcomingVisitNotifications();
	}

}