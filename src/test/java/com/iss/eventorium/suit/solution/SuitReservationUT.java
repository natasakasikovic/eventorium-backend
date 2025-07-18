package com.iss.eventorium.suit.solution;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({"com.iss.eventorium.solution.service", "com.iss.eventorium.solution.repository"})
public class SuitReservationUT { }