package com.iss.eventorium.users.controllers;

import com.iss.eventorium.users.dtos.GetAccountDto;
import com.iss.eventorium.users.dtos.UpdateAccountDto;
import com.iss.eventorium.users.dtos.UpdatedAccountDto;
import com.iss.eventorium.users.models.City;
import com.iss.eventorium.users.models.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final Collection<GetAccountDto> accounts;

    public UserController() {
        accounts = new ArrayList<>();
        accounts.add(new GetAccountDto(1L, "user1@example.com", "John", "Doe", "123456789", "Address 1", "Belgrade", Role.AUTHENTICATED_USER));
        accounts.add(new GetAccountDto(2L, "user2@example.com", "Jane", "Smith", "987654321", "Address 2", "Belgrade", Role.EVENT_ORGANIZER));
        accounts.add(new GetAccountDto(3L, "admin@example.com", "Admin", "User", "555555555", "Admin Street", "Belgrade", Role.ADMIN));
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<GetAccountDto>> getAccounts() {
        // TODO: call service
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAccountDto> getAccount(@PathVariable Long id) {
        // TODO: call service
        GetAccountDto getAccountDto = accounts.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (getAccountDto == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(getAccountDto, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedAccountDto> updateAccount(@PathVariable Long id, @RequestBody UpdateAccountDto account) throws Exception {
        UpdatedAccountDto updatedAccount = new UpdatedAccountDto();
        // TODO: Call service to update the account

        // NOTE: Currently returning nulls as mapping and update logic have not been implemented yet.
        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deactivateAccount(@PathVariable("id") Long id) {
        // TODO: call service
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
