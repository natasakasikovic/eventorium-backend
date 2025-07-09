package com.iss.eventorium.solution.services;

import com.iss.eventorium.company.models.Company;
import com.iss.eventorium.company.repositories.CompanyRepository;
import com.iss.eventorium.event.events.EventDateChangedEvent;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.services.BudgetService;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.shared.exceptions.InsufficientFundsException;
import com.iss.eventorium.shared.exceptions.OwnershipRequiredException;
import com.iss.eventorium.shared.models.EmailDetails;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.shared.services.EmailService;
import com.iss.eventorium.solution.dtos.services.CalendarReservationDto;
import com.iss.eventorium.solution.dtos.services.ReservationRequestDto;
import com.iss.eventorium.solution.dtos.services.ReservationResponseDto;
import com.iss.eventorium.solution.mappers.ReservationMapper;
import com.iss.eventorium.solution.models.Reservation;
import com.iss.eventorium.solution.models.ReservationType;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.repositories.ReservationRepository;
import com.iss.eventorium.solution.specifications.ServiceReservationSpecification;
import com.iss.eventorium.solution.validators.reservation.*;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
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
    private final BudgetService budgetService;

    private final ReservationMapper mapper;

    private final SpringTemplateEngine templateEngine;

    public void createReservation (ReservationRequestDto request, Long eventId, Long serviceId) {
        Event event = eventService.find(eventId);
        assertEventOwnership(event);
        Service service = serviceService.find(serviceId);
        Reservation reservation = mapper.fromRequest(request, event, service);

        validateReservation(reservation, request.getPlannedAmount());
        saveEntity(reservation);
        budgetService.addReservationAsBudgetItem(reservation, request.getPlannedAmount());

        sendEmails(reservation, false);
    }

    private void assertEventOwnership(Event event) {
        User user = authService.getCurrentUser();

        if (!event.getOrganizer().getId().equals(user.getId()))
            throw new OwnershipRequiredException("You cannot make a reservation for an event you are not the organizer of!");
    }

    private void validateReservation(Reservation reservation, double plannedAmount) {

        List<ReservationValidator> validators = List.of(new EventInPastValidator(),
                                                        new ReservationDeadlineValidator(),
                                                        new ServiceDurationValidator(),
                                                        new WorkingHoursValidator(getCompany(reservation.getService())),
                                                        new ReservationConflictValidator(repository));
        for (ReservationValidator validator : validators)
            validator.validate(reservation);

        Service service = reservation.getService();

        if(plannedAmount < service.getPrice() * (1 - service.getDiscount() / 100))
            throw new InsufficientFundsException("You do not have enough funds for this reservation!");
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

    public List<CalendarReservationDto> getProviderReservations() {
        User provider = authService.getCurrentUser();
        return repository.findAll(ServiceReservationSpecification.getProviderReservations(provider)).stream().map(mapper::toCalendarReservation).toList();
    }

    public List<ReservationResponseDto> getPendingReservations() {
        User user = authService.getCurrentUser();
        Specification<Reservation> specification = ServiceReservationSpecification.getPendingReservations(user);
        return repository.findAll(specification).stream().map(ReservationMapper::toResponse).toList();
    }

    @Scheduled(fixedRate = 60000)
    public void checkReservations() {
        List<Reservation> reservations = repository.findAll(ServiceReservationSpecification.checkForReservationsInOneHour());
        for (Reservation reservation: reservations)
            sendEmails(reservation, true);
    }

    private void sendEmails(Reservation reservation, boolean isReminder) {
        if (!isReminder)
            sendEmailToRecipient(reservation, authService.getCurrentUser().getEmail(), false); // to organizer

        sendEmailToRecipient(reservation, reservation.getService().getProvider().getEmail(), isReminder); // to provider
    }

    private void sendEmailToRecipient(Reservation reservation, String recipient, boolean isReminder) {
        EmailDetails emailDetails = createEmailDetails(reservation, recipient, isReminder);
        emailService.sendSimpleMail(emailDetails);
    }

    private EmailDetails createEmailDetails(Reservation reservation, String recipient, boolean isReminder) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(recipient);
        emailDetails.setSubject(isReminder ? "Reminder about reservation" : "Notification about new reservation!");
        emailDetails.setMsgBody(generateEmailContent(reservation, recipient, isReminder));
        return emailDetails;
    }

    public String generateEmailContent(Reservation reservation, String recipient, boolean isReminder) {
        Context context = new Context();
        context.setVariables(getContextVariables(reservation));

        String emailTemplate;

        if (isReminder)
            emailTemplate = "reservation-reminder-provider";
        else if (Objects.equals(authService.getCurrentUser().getEmail(), recipient))
            emailTemplate = reservation.getStatus() == Status.ACCEPTED ? "reservation-accepted-organizer" : "reservation-pending-organizer";
        else
            emailTemplate = reservation.getStatus() == Status.ACCEPTED ? "reservation-accepted-provider" : "reservation-pending-provider";


        return templateEngine.process(emailTemplate, context);
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

    public ReservationResponseDto updateReservation(Long id, Status status) {
        Reservation reservation = find(id);
        reservation.setStatus(status);

        if(status == Status.ACCEPTED)
            budgetService.markAsReserved(reservation);

        return ReservationMapper.toResponse(repository.save(reservation));
    }

    public Reservation find(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
    }

    public List<Reservation> getEventReservations(Event event) {
        Specification<Reservation> specification = ServiceReservationSpecification.getEventReservations(event);
        return repository.findAll(specification);
    }

    @EventListener
    public void handleEventDateChanged(EventDateChangedEvent dateChangedEvent) {
        Event event = eventService.find(dateChangedEvent.getEventId());
        List<Reservation> reservations = getEventReservations(event);

        reservations.stream()
                .filter(reservation -> isCancellable(reservation, event.getDate()))
                .forEach(reservation -> reservation.setIsCanceled(true));

        repository.saveAll(reservations);
    }

    private boolean isCancellable(Reservation reservation, LocalDate date) {
        int deadline = reservation.getService().getCancellationDeadline();
        return !date.minusDays(deadline).isBefore(LocalDate.now());
    }
}