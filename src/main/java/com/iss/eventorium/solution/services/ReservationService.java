package com.iss.eventorium.solution.services;

import com.iss.eventorium.company.models.Company;
import com.iss.eventorium.company.repositories.CompanyRepository;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.shared.services.EmailService;
import com.iss.eventorium.shared.models.EmailDetails;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.dtos.services.ReservationRequestDto;
import com.iss.eventorium.solution.exceptions.InvalidServiceDurationException;
import com.iss.eventorium.solution.exceptions.ReservationConflictException;
import com.iss.eventorium.solution.exceptions.ReservationDeadlineExceededException;
import com.iss.eventorium.solution.exceptions.ReservationOutsideWorkingHoursException;
import com.iss.eventorium.solution.mappers.ReservationMapper;
import com.iss.eventorium.solution.models.Reservation;
import com.iss.eventorium.solution.models.ReservationType;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.repositories.ReservationRepository;
import com.iss.eventorium.solution.specifications.ServiceReservationSpecification;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.thymeleaf.spring6.SpringTemplateEngine;

import org.thymeleaf.context.Context;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.DAYS;

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

        validateReservation(reservation, service, event);
        saveEntity(reservation, service);

        sendConfirmationMails(reservation, service);
    }

    private void validateReservation(Reservation reservation, Service service, Event event) {
        checkWorkingHours(reservation, getCompany(service));
        checkReservationDurationValidity(reservation, service);
        checkReservationConflicts(reservation, service, event);
        checkReservationDeadline(service, event);
    }

    private void checkReservationDeadline(Service service, Event event) {
        long daysUntilEvent = DAYS.between(LocalDate.now(), event.getDate());
        if (daysUntilEvent < service.getReservationDeadline())
            throw new ReservationDeadlineExceededException("Reservation deadline has passed for this service!");
    }

    private void checkWorkingHours(Reservation reservation, Company company) {
        boolean isBeforeOpeningTime = reservation.getStartingTime().isBefore(company.getOpeningHours());
        boolean isAfterOpeningTime = reservation.getEndingTime().isAfter(company.getClosingHours());

        if (isBeforeOpeningTime || isAfterOpeningTime)
            throw new ReservationOutsideWorkingHoursException(String.format(String.format("Reservations can only be made between %s and %s", company.getOpeningHours(), company.getClosingHours())));
    }

    private void checkReservationConflicts(Reservation reservation, Service service, Event event) {
        Specification<Reservation> specification = ServiceReservationSpecification.checkForOverlappingReservations(event, service, reservation);
        if (repository.exists(specification))
            throw new ReservationConflictException("The selected time slot for this service is already occupied. Please choose a different time.");
    }

    private void checkReservationDurationValidity(Reservation reservation, Service service) {
        long hours = Duration.between(reservation.getStartingTime(), reservation.getEndingTime()).toHours();
        int minDuration = service.getMinDuration();
        int maxDuration = service.getMaxDuration();

        if (minDuration == maxDuration && hours != minDuration)
                throw new InvalidServiceDurationException(String.format("The service duration must be exactly %d hours.", minDuration));

        if (minDuration != maxDuration && ( hours < minDuration || hours > maxDuration))
            throw new InvalidServiceDurationException(String.format("The service duration must be between %d and %d hours.", minDuration, maxDuration));
    }

    private void saveEntity(Reservation reservation, Service service) {
        if (service.getType() == ReservationType.AUTOMATIC)
            reservation.setStatus(Status.ACCEPTED);
        repository.save(reservation);
    }

    private Company getCompany(Service service) {
        User provider = service.getProvider();
        return companyRepository.getCompanyByProviderId(provider.getId());
    }

    private void sendConfirmationMails(Reservation reservation, Service service) {
        sendConfirmation(reservation, service.getProvider().getEmail()); // to provider
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