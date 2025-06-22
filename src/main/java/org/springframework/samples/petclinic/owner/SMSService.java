package org.springframework.samples.petclinic.owner;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SMSService {

	@Value("${twilio.account.sid}")
	private String accountSid;

	@Value("${twilio.auth.token}")
	private String authToken;

	@Value("${twilio.phone.number}")
	private String fromNumber;

	public void sendSMS(String toNumber, String message) {
		Twilio.init(accountSid, authToken);
		Message.creator(new PhoneNumber(toNumber), new PhoneNumber(fromNumber), message).create();
	}

}