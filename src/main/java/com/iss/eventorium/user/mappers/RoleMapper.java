package com.iss.eventorium.user.mappers;

import com.iss.eventorium.user.dtos.role.RoleDto;
import com.iss.eventorium.user.models.Role;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public RoleMapper(ModelMapper modelMapper) { RoleMapper.modelMapper = modelMapper; }

    public static Role fromRequest(RoleDto roleDto) {
        return modelMapper.map(roleDto, Role.class);
    }

    public static RoleDto toResponse(Role role) {
        return modelMapper.map(role, RoleDto.class);
    }

}
