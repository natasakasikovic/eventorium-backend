package com.iss.eventorium.suit.solution;

import com.iss.eventorium.solution.controller.ReservationControllerIntegrationTest;
import com.iss.eventorium.solution.service.ReservationServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses( {ReservationServiceTest.class, ReservationControllerIntegrationTest.class} )
public class SuitServiceReservation { }