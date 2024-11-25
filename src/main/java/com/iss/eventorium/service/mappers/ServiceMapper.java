package com.iss.eventorium.service.mappers;

import com.iss.eventorium.service.dtos.CreateServiceRequestDto;
import com.iss.eventorium.service.dtos.ServiceSummaryResponseDto;
import com.iss.eventorium.service.dtos.ServiceRequestDto;
import com.iss.eventorium.service.dtos.ServiceResponseDto;
import com.iss.eventorium.service.models.Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public ServiceMapper(ModelMapper modelMapper) {
        ServiceMapper.modelMapper = modelMapper;
    }

    public static Service fromCreateRequest(CreateServiceRequestDto request) {
        return modelMapper.map(request, Service.class);
    }

    public static Service fromRequest(ServiceRequestDto request) {
        return modelMapper.map(request, Service.class);
    }

    public static ServiceResponseDto toResponse(Service service) {
        return modelMapper.map(service, ServiceResponseDto.class);
    }

    public static ServiceSummaryResponseDto toSummaryResponse(Service service) {
        return modelMapper.map(service, ServiceSummaryResponseDto.class);
    }
}
