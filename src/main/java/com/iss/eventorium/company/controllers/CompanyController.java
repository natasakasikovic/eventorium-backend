package com.iss.eventorium.company.controllers;

import com.iss.eventorium.company.dtos.CreateCompanyDto;
import com.iss.eventorium.company.dtos.CreatedCompanyDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {
    private final Collection<CreatedCompanyDto> companies;

    public CompanyController() {
        this.companies = new ArrayList<>();

        // NOTE: for testing
        CreatedCompanyDto company1 = new CreatedCompanyDto(1L, "Example Company1", "123 Main Street", "Example City", "123-456-7890", "A great company!", "contact1@example.com", new ArrayList<>(), Time.valueOf("09:00:00"), Time.valueOf("17:00:00"), 1L);
        CreatedCompanyDto company2 = new CreatedCompanyDto(2L, "Example Company2", "123 Main Street", "Example City", "123-456-7890", "A great company!", "contact2@example.com", new ArrayList<>(), Time.valueOf("09:00:00"), Time.valueOf("17:00:00"), 1L);

        companies.add(company1);
        companies.add(company2);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedCompanyDto> createCompany(@RequestBody CreateCompanyDto companyDto) throws Exception {
        CreatedCompanyDto createdCompanyDto = new CreatedCompanyDto();
        // TODO: Call service

        return new ResponseEntity<>(createdCompanyDto, HttpStatus.OK);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CreatedCompanyDto>> getAll() {
        // TODO: call service
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreatedCompanyDto> getCompany(@PathVariable Long id) {
        // TODO: call service
        CreatedCompanyDto createdCompanyDto = companies.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (createdCompanyDto == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(createdCompanyDto, HttpStatus.OK);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedCompanyDto> updateCompany(@PathVariable Long id, @RequestBody CreatedCompanyDto company) throws Exception {
        CreatedCompanyDto updatedCompany = new CreatedCompanyDto();
        // TODO: Call service to update the company

        // NOTE: Currently returning nulls as mapping and update logic have not been implemented yet.
        return new ResponseEntity<>(updatedCompany, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable("id") Long id) {
        // TODO: call service
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
