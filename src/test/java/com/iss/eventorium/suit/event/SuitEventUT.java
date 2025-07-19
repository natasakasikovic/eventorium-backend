package com.iss.eventorium.suit.event;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({"com.iss.eventorium.event.service", "com.iss.eventorium.event.repository"})
public class SuitEventUT {
}
