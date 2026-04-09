package com.studioparametric.officeservice.controller;

import com.studioparametric.officeservice.entity.Category;
import com.studioparametric.officeservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        log.info("GET /api/categories - Fetching all categories");
        List<Category> categories = categoryService.getAllCategories();
        log.info("GET /api/categories - Found {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }
}
