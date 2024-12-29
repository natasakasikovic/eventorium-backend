package com.iss.eventorium.solution.mappers;

import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.dtos.pricelists.PriceListResponseDto;
import com.iss.eventorium.solution.models.Solution;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PriceListMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public PriceListMapper(ModelMapper modelMapper) {
        PriceListMapper.modelMapper = modelMapper;
    }


    public static<T extends Solution> PriceListResponseDto toResponse(T solution) {
        return PriceListResponseDto.builder()
                .id(solution.getId())
                .name(solution.getName())
                .price(solution.getPrice())
                .discount(solution.getDiscount())
                .netPrice(solution.getPrice() * (1 - solution.getDiscount() / 100))
                .build();
    }

    public static<T extends Solution> PagedResponse<PriceListResponseDto> toPagedResponse(Page<T> page) {
        return new PagedResponse<>(
                page.stream().map(PriceListMapper::toResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

}
