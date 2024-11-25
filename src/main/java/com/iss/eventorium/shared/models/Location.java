package com.iss.eventorium.shared.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
}
