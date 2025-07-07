package com.iss.eventorium.shared.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void anyServiceMethod() {}

    @Before("anyServiceMethod() && !@annotation(SkipFilter)")
    public void enableFilter() {
        Session session = entityManager.unwrap(Session.class);
        if (session.getEnabledFilter("activeFilter") == null)
            session.enableFilter("activeFilter").setParameter("isDeleted", false);
    }

}
