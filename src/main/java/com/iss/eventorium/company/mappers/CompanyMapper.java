package com.iss.eventorium.company.mappers;

import com.iss.eventorium.company.dtos.CompanyDetailsDto;
import com.iss.eventorium.company.dtos.CompanyRequestDto;
import com.iss.eventorium.company.dtos.CompanyResponseDto;
import com.iss.eventorium.company.dtos.ProviderCompanyDto;
import com.iss.eventorium.company.models.Company;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyMapper {

    private final ModelMapper modelMapper;

    public Company fromRequest(CompanyRequestDto companyRequestDto) {
        return modelMapper.map(companyRequestDto, Company.class);
    }

    public CompanyResponseDto toResponse(Company company) {
        return modelMapper.map(company, CompanyResponseDto.class);
    }

    public ProviderCompanyDto toProviderCompanyResponse(Company company) {
        return modelMapper.map(company, ProviderCompanyDto.class);
    }

    public CompanyDetailsDto toCompanyDetailsResponse(Company company) {
        return modelMapper.map(company, CompanyDetailsDto.class);
    }
}
