package com.iss.eventorium.category.controllers;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.category.services.CategoryService;
import com.iss.eventorium.shared.utils.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.iss.eventorium.category.mappers.CategoryMapper.fromRequest;
import static com.iss.eventorium.category.mappers.CategoryMapper.toResponse;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponseDto>> getCategories() {
        return  ResponseEntity
                .ok(categoryService
                        .getCategories().stream()
                        .map(CategoryMapper::toResponse)
                        .toList());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<CategoryResponseDto>> getCategoriesPaged(Pageable pageable) {
        return ResponseEntity
                .ok(CategoryMapper
                        .toPagedResponse(categoryService.getCategoriesPaged(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategory(@PathVariable Long id) {
        return ResponseEntity
                .ok(toResponse(categoryService.getCategory(id)));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody CategoryRequestDto requestDto) {
        return ResponseEntity
                .ok(toResponse(categoryService.createCategory(fromRequest(requestDto))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @RequestBody CategoryRequestDto requestDto,
            @PathVariable Long id
    ) {
        return ResponseEntity
                .ok(toResponse(categoryService.updateCategory(id, fromRequest(requestDto))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
