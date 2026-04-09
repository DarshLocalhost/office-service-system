package com.studioparametric.officeservice.repository;

import com.studioparametric.officeservice.entity.OptionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionValueRepository extends JpaRepository<OptionValue, Long> {
    List<OptionValue> findByOptionId(Long optionId);
}
