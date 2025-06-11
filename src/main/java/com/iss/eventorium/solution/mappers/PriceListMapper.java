package com.iss.eventorium.solution.mappers;

import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.dtos.pricelists.PriceListResponseDto;
import com.iss.eventorium.solution.models.Solution;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PriceListMapper {

    public <T extends Solution> PriceListResponseDto toResponse(T solution) {
        return PriceListResponseDto.builder()
                .id(solution.getId())
                .name(solution.getName())
                .price(solution.getPrice())
                .discount(solution.getDiscount())
                .netPrice(solution.getPrice() * (1 - solution.getDiscount() / 100))
                .build();
    }

    public <T extends Solution> PagedResponse<PriceListResponseDto> toPagedResponse(Page<T> page) {
        return new PagedResponse<>(
                page.stream().map(this::toResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

}
