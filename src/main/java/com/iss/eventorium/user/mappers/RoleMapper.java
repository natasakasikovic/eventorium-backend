package com.iss.eventorium.user.mappers;

import com.iss.eventorium.user.dtos.role.RoleDto;
import com.iss.eventorium.user.models.Role;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    private final ModelMapper modelMapper;

    public Role fromRequest(RoleDto roleDto) {
        return modelMapper.map(roleDto, Role.class);
    }

    public RoleDto toResponse(Role role) {
        return modelMapper.map(role, RoleDto.class);
    }

}
