package com.iss.eventorium.event.services;

import com.iss.eventorium.event.dtos.invitation.InvitationDetailsDto;
import com.iss.eventorium.event.dtos.invitation.InvitationRequestDto;
import com.iss.eventorium.event.dtos.invitation.InvitationResponseDto;
import com.iss.eventorium.event.mappers.InvitationMapper;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.models.Invitation;
import com.iss.eventorium.event.repositories.InvitationRepository;
import com.iss.eventorium.shared.models.EmailDetails;
import com.iss.eventorium.shared.services.EmailService;
import com.iss.eventorium.shared.utils.HashUtils;
import com.iss.eventorium.user.exceptions.EmailAlreadyTakenException;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import com.iss.eventorium.user.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final EventService eventService;
    private final AuthService authService;
    private final SpringTemplateEngine templateEngine;

    @Value("${frontend.url}")
    public String BASE_URI;

    public static final String EVENT_INVITATION_AUTHENTICATED_TEMPLATE = "event-invitation-authenticated";
    public static final String EVENT_INVITATION_UNAUTHENTICATED_TEMPLATE = "event-invitation-unauthenticated";
    public static final String EMAIL_SUBJECT = "Invitation from Eventorium";

    public void sendInvitations(List<InvitationRequestDto> invitationsDto, Long id) {
        Event event = eventService.find(id);

        List<Invitation> invitations = invitationsDto.stream()
                .map(InvitationMapper::fromRequest)
                .peek(invitation -> invitation.setEvent(event)).toList();

        for (Invitation invitation : invitations) {
            if (!userService.existsByEmail(invitation.getEmail()))
                invitation.setHash(HashUtils.generateHash());

            EmailDetails emailDetails = createEmailDetails(invitation);
            emailService.sendSimpleMail(emailDetails);
        }
        eventService.setIsDraftFalse(event);
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
        String link = (invitation.getHash() == null) ? BASE_URI + "/login" : BASE_URI + "/quick-registration/" + invitation.getHash();
        variables.put("LINK", link);
        return variables;
    }

    public void verifyInvitation(String hash) {
        findByHash(hash);
    }

    public InvitationResponseDto getInvitation(String hash){
        Invitation invitation = findByHash(hash);
        InvitationResponseDto response = InvitationMapper.toResponse(invitation);

        if (userService.existsByEmail(invitation.getEmail()))
            throw new EmailAlreadyTakenException("An email you are trying to use within this invitation is already associated with an account. \nPlease log in to your account.");

        return response;
    }

    private Invitation findByHash(String hash){
        return repository.findByHash(hash).orElseThrow(() -> new EntityNotFoundException("Invitation not found"));
    }

    private List<Invitation> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<InvitationDetailsDto> getInvitations() {
        User user = authService.getCurrentUser();
        return findByEmail(user.getEmail()).stream().map(InvitationMapper::toInvitationDetails).toList();
    }
}