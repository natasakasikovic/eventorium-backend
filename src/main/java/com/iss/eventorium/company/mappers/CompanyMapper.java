package com.iss.eventorium.company.mappers;

import com.iss.eventorium.company.dtos.CompanyDetailsDto;
import com.iss.eventorium.company.dtos.CompanyRequestDto;
import com.iss.eventorium.company.dtos.CompanyResponseDto;
import com.iss.eventorium.company.dtos.ProviderCompanyDto;
import com.iss.eventorium.company.models.Company;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public CompanyMapper(ModelMapper modelMapper) { CompanyMapper.modelMapper = modelMapper; }

    public static Company fromRequest(CompanyRequestDto companyRequestDto) {
        return modelMapper.map(companyRequestDto, Company.class);
    }

    public static CompanyResponseDto toResponse(Company company) {
        return modelMapper.map(company, CompanyResponseDto.class);
    }

    public static ProviderCompanyDto toProviderCompanyResponse(Company company) {
        return modelMapper.map(company, ProviderCompanyDto.class);
    }

    public static CompanyDetailsDto toCompanyDetailsResponse(Company company) {
        return modelMapper.map(company, CompanyDetailsDto.class);
    }
}
