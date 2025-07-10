package main.java.com.securefiles.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;

    @Value("${twilio.phone_number}")
    private String fromNumber;

    public void sendSms(String to, String body) {
        Twilio.init(accountSid, authToken);
        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(fromNumber),
                body).create();
    }
}
