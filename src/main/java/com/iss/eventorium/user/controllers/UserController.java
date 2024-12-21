package com.iss.eventorium.user.controllers;

import com.iss.eventorium.user.dtos.GetAccountDto;
import com.iss.eventorium.user.dtos.ResetPasswordRequestDto;
import com.iss.eventorium.user.dtos.UpdateAccountDto;
import com.iss.eventorium.user.dtos.UpdatedAccountDto;
import com.iss.eventorium.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final Collection<GetAccountDto> accounts = new ArrayList<>();

    @PostMapping("/reset-password/{id}")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id,
                                              @RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        // TODO: Implement service to validate old password, match new and confirm passwords,
        //  and return Bad Request if validation fails
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
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
