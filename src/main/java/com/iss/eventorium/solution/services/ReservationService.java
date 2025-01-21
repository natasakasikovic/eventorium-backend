package com.iss.eventorium.solution.services;

import com.iss.eventorium.company.models.Company;
import com.iss.eventorium.company.repositories.CompanyRepository;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.shared.services.EmailService;
import com.iss.eventorium.shared.models.EmailDetails;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.dtos.services.ReservationRequestDto;
import com.iss.eventorium.solution.mappers.ReservationMapper;
import com.iss.eventorium.solution.models.Reservation;
import com.iss.eventorium.solution.models.ReservationType;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.repositories.ReservationRepository;
import com.iss.eventorium.solution.validators.reservation.*;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;

import lombok.RequiredArgsConstructor;

import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repository;
    private final CompanyRepository companyRepository;
    private final EventService eventService;
    private final ServiceService serviceService;
    private final EmailService emailService;
    private final AuthService authService;
    private final SpringTemplateEngine templateEngine;

    public void createReservation (ReservationRequestDto request, Long eventId, Long serviceId) {
        Event event = eventService.find(eventId); // TODO: reservations can be made only for draft events
        Service service = serviceService.find(serviceId);
        Reservation reservation = ReservationMapper.fromRequest(request, event, service);

        validateReservation(reservation);
        saveEntity(reservation);

        sendConfirmationMails(reservation);
    }

    private void validateReservation(Reservation reservation) {
        List<ReservationValidator> validators = List.of(new WorkingHoursValidator(getCompany(reservation.getService())),
                                                        new ServiceDurationValidator(),
                                                        new ReservationConflictValidator(repository),
                                                        new ReservationDeadlineValidator());
        for (ReservationValidator validator : validators)
            validator.validate(reservation);
    }

    private void saveEntity(Reservation reservation) {
        if (reservation.getService().getType() == ReservationType.AUTOMATIC)
            reservation.setStatus(Status.ACCEPTED);
        repository.save(reservation);
    }

    private Company getCompany(Service service) {
        User provider = service.getProvider();
        return companyRepository.getCompanyByProviderId(provider.getId());
    }

    private void sendConfirmationMails(Reservation reservation) {
        sendConfirmation(reservation, reservation.getService().getProvider().getEmail()); // to provider
        sendConfirmation(reservation, authService.getCurrentUser().getEmail()); // to organizer
    }

    private void sendConfirmation(Reservation reservation, String recipient) {
        EmailDetails emailDetails = createEmailDetails(reservation, recipient);
        emailService.sendSimpleMail(emailDetails);
    }

    private EmailDetails createEmailDetails(Reservation reservation, String recipient) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(recipient);
        emailDetails.setSubject("Notification about new reservation!");
        emailDetails.setMsgBody(generateEmailContent(reservation, recipient));
        return emailDetails;
    }

    public String generateEmailContent(Reservation reservation, String recipient) {
        Context context = new Context();
        context.setVariables(getContextVariables(reservation));

        String emailTemplate = determineEmailTemplate(reservation, recipient);
        return templateEngine.process(emailTemplate, context);
    }

    private String determineEmailTemplate(Reservation reservation, String recipient) {
        if (Objects.equals(authService.getCurrentUser().getEmail(), recipient))
            return reservation.getStatus() == Status.ACCEPTED ? "reservation-accepted-organizer" : "reservation-pending-organizer";
        else
            return reservation.getStatus() == Status.ACCEPTED ? "reservation-accepted-provider" : "reservation-pending-provider";
    }

    private Map<String, Object> getContextVariables(Reservation reservation) {
        Map<String, Object> variables = new HashMap<>();
        Event event = reservation.getEvent();
        variables.put("serviceName", reservation.getService().getName());
        variables.put("location", String.format("%s, %s", event.getAddress(), event.getCity().getName()));
        variables.put("eventDate", event.getDate());
        variables.put("startTime", reservation.getStartingTime());
        variables.put("endTime", reservation.getEndingTime());
        return variables;
    }
}