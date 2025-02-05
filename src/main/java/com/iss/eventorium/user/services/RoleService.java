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

    private final RoleRepository repository;

    public Role findById(Long id) {
        return this.repository.getReferenceById(id);
    }

    public List<Role> findByName(String name) {
        return this.repository.findByName(name);
    }

    public List<RoleDto> getRegistrationRoles() {
        List<Role> roles = repository.findByNameIn(List.of("PROVIDER", "EVENT_ORGANIZER"));
        return roles.stream()
                .map(RoleMapper::toResponse)
                .toList();
    }

}
