package com.studioparametric.officeservice.controller;

import com.studioparametric.officeservice.entity.Item;
import com.studioparametric.officeservice.entity.ItemOption;
import com.studioparametric.officeservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<Item>> getItemsByCategory(@RequestParam Long categoryId) {
        log.info("GET /api/items?categoryId={} - Fetching items", categoryId);
        List<Item> items = itemService.getItemsByCategory(categoryId);
        log.info("GET /api/items?categoryId={} - Found {} items", categoryId, items.size());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}/options")
    public ResponseEntity<List<ItemOption>> getItemOptions(@PathVariable Long id) {
        log.info("GET /api/items/{}/options - Fetching item options", id);
        List<ItemOption> options = itemService.getItemOptions(id);
        log.info("GET /api/items/{}/options - Found {} options", id, options.size());
        return ResponseEntity.ok(options);
    }
}
