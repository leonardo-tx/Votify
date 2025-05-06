package br.com.votify.core.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class TestEmailService extends EmailService {
    private String lastSentCode;

    public TestEmailService() {
        super(null);
    }

    @Override
    public void sendEmailConfirmation(String to, String confirmationCode) {
        this.lastSentCode = confirmationCode;
    }

    public String getLastSentCode() {
        return lastSentCode;
    }
}
