package com.iss.eventorium.solution.models;

import com.iss.eventorium.shared.models.Solution;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceList {
    private Long id;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private List<Solution> solutions;
}
