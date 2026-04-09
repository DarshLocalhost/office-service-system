package com.studioparametric.officeservice.repository;

import com.studioparametric.officeservice.entity.RequestOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestOptionRepository extends JpaRepository<RequestOption, Long> {
    List<RequestOption> findByRequestId(Long requestId);
}
