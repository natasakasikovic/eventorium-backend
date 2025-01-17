package com.iss.eventorium.user.services;

import com.iss.eventorium.user.dtos.role.RoleDto;
import com.iss.eventorium.user.mappers.RoleMapper;
import com.iss.eventorium.user.models.Role;
import com.iss.eventorium.user.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role findById(Long id) {
        Role auth = this.roleRepository.getReferenceById(id);
        return auth;
    }

    public List<Role> findByName(String name) {
        List<Role> roles = this.roleRepository.findByName(name);
        return roles;
    }

    public List<RoleDto> getRegistrationRoles() {
        List<Role> roles = roleRepository.findByNameIn(List.of("PROVIDER", "EVENT_ORGANIZER"));
        return roles.stream()
                .map(RoleMapper::toResponse)
                .toList();
    }

}
