package com.iss.eventorium.shared.services;

import com.iss.eventorium.shared.exceptions.EmailSendingException;
import com.iss.eventorium.shared.models.EmailDetails;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}") private String sender;

    public void sendSimpleMail(EmailDetails emailDetails) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(emailDetails.getRecipient());
            helper.setSubject(emailDetails.getSubject());
            helper.setText(emailDetails.getMsgBody(), true);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendingException(String.format("Failed to send email to %s", emailDetails.getRecipient()));
        }
    }

}
