package com.iss.eventorium.category.controllers;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.services.CategoryService;
import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.controllers.ServiceController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
@CrossOrigin
public class CategoryController {

    private final CategoryService categoryService;
    private final ServiceController serviceController;

    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponseDto>> getCategories() {
        return  ResponseEntity.ok(categoryService.getCategories());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<CategoryResponseDto>> getCategoriesPaged(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getCategoriesPaged(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
        return new ResponseEntity<>(categoryService.createCategory(requestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @Valid @RequestBody CategoryRequestDto requestDto,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
