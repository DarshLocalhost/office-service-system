package com.studioparametric.officeservice.controller;

import com.studioparametric.officeservice.dto.RequestDto;
import com.studioparametric.officeservice.dto.StatusUpdateRequest;
import com.studioparametric.officeservice.entity.Request;
import com.studioparametric.officeservice.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<Request> createRequest(@RequestBody RequestDto dto) {
        log.info("POST /api/requests - Creating request for item: {}", dto.getItemId());
        Request request = requestService.createRequest(dto);
        log.info("POST /api/requests - Created request with id: {}", request.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }

    @GetMapping
    public ResponseEntity<List<Request>> getAllRequests() {
        log.info("GET /api/requests - Fetching all requests");
        List<Request> requests = requestService.getAllRequests();
        log.info("GET /api/requests - Found {} requests", requests.size());
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Request> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest statusRequest) {
        log.info("PUT /api/requests/{}/status - Updating status to {}", id, statusRequest.getStatus());
        Request updatedRequest = requestService.updateRequestStatus(id, statusRequest.getStatus());
        log.info("PUT /api/requests/{}/status - Status updated to {}", id, updatedRequest.getStatus());
        return ResponseEntity.ok(updatedRequest);
    }
}
