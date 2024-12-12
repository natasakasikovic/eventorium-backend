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

    // TODO: EXTRACT HARDCODED VALUES IN CODE INTO CONSTANTS!
    String baseURI = "http://localhost:8080/api/v1/";

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
        emailDetails.setSubject("Invitation from Eventorium");
        emailDetails.setMsgBody(generateEmailContent(invitation));
        return emailDetails;
    }

    public String generateEmailContent(Invitation invitation) {
        Context context = new Context();
        context.setVariables(getContextVariables(invitation));
        String emailTemplate = (invitation.getHash() == null) ? "event-invitation-authenticated" : "event-invitation-unauthenticated";
        return templateEngine.process(emailTemplate, context);
    }

    private Map<String, Object> getContextVariables(Invitation invitation) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("eventName", invitation.getEvent().getName());
        variables.put("eventType", invitation.getEvent().getType());
        variables.put("eventDate", invitation.getEvent().getDate());
        variables.put("eventAddress", invitation.getEvent().getAddress());
        String link = (invitation.getHash() == null) ? baseURI + "login" : baseURI + invitation.getHash();
        variables.put("link", link);
        return variables;
    }

}