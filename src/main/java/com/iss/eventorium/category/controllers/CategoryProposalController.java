package com.iss.eventorium.category.controllers;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.dtos.UpdateStatusRequestDto;
import com.iss.eventorium.category.services.CategoryProposalService;
import com.iss.eventorium.category.services.CategoryService;
import com.iss.eventorium.shared.models.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/categories/pending")
public class CategoryProposalController {

    private final CategoryProposalService categoryProposalService;
    private final CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponseDto>> getPendingCategories() {
        return ResponseEntity.ok(categoryService.getPendingCategories());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<CategoryResponseDto>> getPendingCategoriesPaged(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getPendingCategoriesPaged(pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategoryStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequestDto dto
    ) {
        return ResponseEntity.ok(categoryProposalService.updateCategoryStatus(id, dto.getStatus()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategoryProposal(
            @PathVariable Long id,
            @RequestBody CategoryRequestDto dto
    ) {
        return ResponseEntity.ok(categoryProposalService.updateCategoryProposal(id, dto));
    }

    @PutMapping("/{id}/change")
    public ResponseEntity<CategoryResponseDto> changeCategoryProposal(
            @PathVariable Long id,
            @RequestBody CategoryRequestDto dto
    ) {
        return ResponseEntity.ok(categoryProposalService.changeCategoryProposal(id, dto));
    }

}
