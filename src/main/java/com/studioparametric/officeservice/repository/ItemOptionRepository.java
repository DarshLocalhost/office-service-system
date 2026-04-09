package com.studioparametric.officeservice.repository;

import com.studioparametric.officeservice.entity.Item;
import com.studioparametric.officeservice.entity.ItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemOptionRepository extends JpaRepository<ItemOption, Long> {
    List<ItemOption> findByItemId(Long itemId);
    boolean existsByItemAndName(Item item, String name);
}
