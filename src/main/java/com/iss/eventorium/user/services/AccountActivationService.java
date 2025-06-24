package com.iss.eventorium.user.services;

import com.iss.eventorium.shared.models.EmailDetails;
import com.iss.eventorium.shared.services.EmailService;
import com.iss.eventorium.user.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountActivationService {

    private final SpringTemplateEngine templateEngine;
    private final EmailService emailService;

    public static final String EMAIL_SUBJECT = "Email verification";
    public static final String EMAIL_TEMPLATE = "account-activation";

    @Value("${backend.url}")
    public String backendUrl;

    public void sendActivationEmail(User user) {
        EmailDetails emailDetails = createEmailDetails(user);
        emailService.sendSimpleMail(emailDetails);
    }

    private EmailDetails createEmailDetails(User user) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(user.getEmail());
        emailDetails.setSubject(EMAIL_SUBJECT);
        emailDetails.setMsgBody(generateEmailContent(user));
        return emailDetails;
    }

    public String generateEmailContent(User user) {
        Context context = new Context();
        context.setVariables(getContextVariables(user));
        return templateEngine.process(EMAIL_TEMPLATE, context);
    }

    private Map<String, Object> getContextVariables(User user) {
        Map<String, Object> variables = new HashMap<>();
        String link = String.format("%s/api/v1/auth/activation/%s", backendUrl, user.getHash());
        variables.put("LINK", link);
        return variables;
    }
}
