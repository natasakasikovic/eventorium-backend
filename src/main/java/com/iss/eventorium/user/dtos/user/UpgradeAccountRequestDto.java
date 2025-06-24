package com.iss.eventorium.user.dtos.user;

import com.iss.eventorium.user.dtos.role.RoleDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpgradeAccountRequestDto {

    @NotNull(message = "Role is required")
    private RoleDto role;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+)?\\d{9,15}$", message = "Invalid phone number format")
    private String phoneNumber;
}
