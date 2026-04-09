package com.studioparametric.officeservice.service;

import com.studioparametric.officeservice.entity.Item;
import com.studioparametric.officeservice.entity.ItemOption;
import com.studioparametric.officeservice.repository.ItemOptionRepository;
import com.studioparametric.officeservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;

    @Transactional(readOnly = true)
    public List<Item> getItemsByCategory(Long categoryId) {
        log.info("Fetching items for category: {}", categoryId);
        return itemRepository.findByCategoryIdAndIsActiveTrue(categoryId);
    }

    @Transactional(readOnly = true)
    public List<ItemOption> getItemOptions(Long itemId) {
        log.info("Fetching options for item: {}", itemId);
        return itemOptionRepository.findByItemId(itemId);
    }
}
