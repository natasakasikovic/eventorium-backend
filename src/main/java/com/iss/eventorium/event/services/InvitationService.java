package com.iss.eventorium.event.services;

import com.iss.eventorium.event.dtos.InvitationRequestDto;
import com.iss.eventorium.event.mappers.InvitationMapper;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.models.Invitation;
import com.iss.eventorium.event.repositories.InvitationRepository;
import com.iss.eventorium.shared.email.EmailDetails;
import com.iss.eventorium.shared.email.EmailService;
import com.iss.eventorium.shared.utils.HashUtils;
import com.iss.eventorium.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository repository;
    private final UserService userService;
    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;

    // NOTE: If constants are used in other files as well, consider creating a dedicated constants class!
    public static final String BASE_URI = "http://localhost:8080/api/v1/";
    public static final String EVENT_INVITATION_AUTHENTICATED_TEMPLATE = "event-invitation-authenticated";
    public static final String EVENT_INVITATION_UNAUTHENTICATED_TEMPLATE = "event-invitation-unauthenticated";
    public static final String EMAIL_SUBJECT = "Invitation from Eventorium";

    public void sendInvitations(List<InvitationRequestDto> invitationsDto, Event event) {
        List<Invitation> invitations = invitationsDto.stream()
                .map(InvitationMapper::fromRequest)
                .peek(invitation -> invitation.setEvent(event)).toList();

        for (Invitation invitation : invitations) {
            if (!userService.existsByEmail(invitation.getEmail()))
                invitation.setHash(HashUtils.generateHash());

            EmailDetails emailDetails = createEmailDetails(invitation);
            emailService.sendSimpleMail(emailDetails);
        }
        repository.saveAll(invitations);
    }

    private EmailDetails createEmailDetails(Invitation invitation) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(invitation.getEmail());
        emailDetails.setSubject(EMAIL_SUBJECT);
        emailDetails.setMsgBody(generateEmailContent(invitation));
        return emailDetails;
    }

    public String generateEmailContent(Invitation invitation) {
        Context context = new Context();
        context.setVariables(getContextVariables(invitation));
        String emailTemplate = (invitation.getHash() == null) ? EVENT_INVITATION_AUTHENTICATED_TEMPLATE : EVENT_INVITATION_UNAUTHENTICATED_TEMPLATE;
        return templateEngine.process(emailTemplate, context);
    }

    private Map<String, Object> getContextVariables(Invitation invitation) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("EVENT_NAME", invitation.getEvent().getName());
        variables.put("EVENT_DATE", invitation.getEvent().getDate());
        variables.put("EVENT_ADDRESS", invitation.getEvent().getAddress());
        String link = (invitation.getHash() == null) ? BASE_URI + "login" : BASE_URI + invitation.getHash();
        variables.put("LINK", link);
        return variables;
    }

}