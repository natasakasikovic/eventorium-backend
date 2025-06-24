package com.iss.eventorium.company.repositories;

import com.iss.eventorium.company.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company getCompanyByProviderId(Long id);
}
