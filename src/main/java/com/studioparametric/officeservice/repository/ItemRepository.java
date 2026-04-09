package com.studioparametric.officeservice.repository;

import com.studioparametric.officeservice.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategoryIdAndIsActiveTrue(Long categoryId);
    Optional<Item> findByName(String name);
}
