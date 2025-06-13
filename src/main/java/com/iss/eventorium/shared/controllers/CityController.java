package com.iss.eventorium.shared.controllers;

import com.iss.eventorium.shared.api.CityApi;
import com.iss.eventorium.shared.dtos.CityDto;
import com.iss.eventorium.shared.services.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/cities")
public class CityController implements CityApi {

    private final CityService cityService;

    @GetMapping("/all")
    public ResponseEntity<List<CityDto>> getAllCities() {
        return ResponseEntity.ok(cityService.getAll());
    }
}
