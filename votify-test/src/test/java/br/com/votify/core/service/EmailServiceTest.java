package br.com.votify.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_CODE = "123456";
    private static final String TEST_SENDER = "sender@example.com";

    @BeforeEach
    public void setupBeforeEach() {
        ReflectionTestUtils.setField(emailService, "senderEmail", TEST_SENDER);
        emailService.setMailSender(mailSender);
    }

    @Test
    public void sendEmailConfirmation_WhenEmailConfigured_ShouldSendEmail() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmailConfirmation(TEST_EMAIL, TEST_CODE);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void sendEmailConfirmation_WhenNoSenderEmail_ShouldNotSendEmail() {
        ReflectionTestUtils.setField(emailService, "senderEmail", "");

        emailService.sendEmailConfirmation(TEST_EMAIL, TEST_CODE);

        verifyNoInteractions(mailSender);
    }

    @Test
    public void sendEmailConfirmation_WhenNoMailSender_ShouldNotSendEmail() {
        emailService.setMailSender(null);

        emailService.sendEmailConfirmation(TEST_EMAIL, TEST_CODE);

        verifyNoInteractions(mailSender);
    }

    @Test
    public void sendEmailConfirmation_ShouldSetCorrectMessageProperties() {
        doAnswer(invocation -> {
            SimpleMailMessage message = invocation.getArgument(0);
            assertEquals(TEST_SENDER, message.getFrom());
            assertEquals(TEST_EMAIL, message.getTo()[0]);
            assertEquals("Votify - Confirmation Code", message.getSubject());
            assertTrue(message.getText().contains(TEST_CODE));
            return null;
        }).when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmailConfirmation(TEST_EMAIL, TEST_CODE);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
} 