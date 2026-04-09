package com.studioparametric.officeservice.service;

import com.studioparametric.officeservice.entity.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void notifyStaff(Request request) {
        if (request.getAssignedTo() != null) {
            log.info("[NOTIFICATION] New request #{} assigned to staff: {} (email: {})",
                    request.getId(),
                    request.getAssignedTo().getName(),
                    request.getAssignedTo().getEmail());
        }
    }

    public void notifyEmployee(Request request) {
        log.info("[NOTIFICATION] Request #{} status updated to {} for employee: {} (email: {})",
                request.getId(),
                request.getStatus(),
                request.getCreatedBy().getName(),
                request.getCreatedBy().getEmail());
    }
}
