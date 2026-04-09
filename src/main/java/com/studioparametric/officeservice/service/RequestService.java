package com.studioparametric.officeservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studioparametric.officeservice.dto.RequestDto;
import com.studioparametric.officeservice.entity.Item;
import com.studioparametric.officeservice.entity.Request;
import com.studioparametric.officeservice.entity.RequestOption;
import com.studioparametric.officeservice.entity.User;
import com.studioparametric.officeservice.exception.ResourceNotFoundException;
import com.studioparametric.officeservice.repository.ItemRepository;
import com.studioparametric.officeservice.repository.RequestOptionRepository;
import com.studioparametric.officeservice.repository.RequestRepository;
import com.studioparametric.officeservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestOptionRepository requestOptionRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;
    private final NotificationService notificationService;

    @Transactional
    public Request createRequest(RequestDto dto) {
        log.info("=== BACKEND REQUEST DEBUG ===");
        log.info("Received RequestDto: {}", dto);
        log.info("Item ID: {}", dto.getItemId());
        log.info("Quantity: {}", dto.getQuantity());
        log.info("Customization: {}", dto.getCustomization());
        log.info("User Data: {}", dto.getUser());
        log.info("Options: {}", dto.getOptions());
        log.info("=== END BACKEND DEBUG ===");

        log.info("Creating new request for item: {}", dto.getItemId());

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + dto.getItemId()));

        // Use frontend user data if provided, fallback to first employee
        User createdBy;
       // if (dto.getUser() != null && dto.getUser().getName() != null) {
        if (dto.getUser() != null && dto.getUser().getName() != null && !dto.getUser().getName().trim().isEmpty()){ 
        // Find or create user based on frontend data
            createdBy = findOrCreateUser(dto.getUser());
            log.info("Request created by frontend user: {} (floor: {})", createdBy.getName(), createdBy.getFloor());
        } else {
            createdBy = userService.getFirstEmployee();
            log.info("Request created by default employee: {} (floor: {})", createdBy.getName(), createdBy.getFloor());
        }

        User assignedTo = findStaffForFloor(createdBy.getFloor());
        if (assignedTo != null) {
            log.info("Auto-assigned to staff: {} (floor: {})", assignedTo.getName(), assignedTo.getFloor());
        }

        Request request = Request.builder()
                .item(item)
                .createdBy(createdBy)
                .assignedTo(assignedTo)
                .quantity(dto.getQuantity())
                .status(Request.RequestStatus.PENDING)
                .customization(dto.getCustomization())
                .floor(createdBy.getFloor()) // Store floor snapshot
                .build();

        Request savedRequest = requestRepository.save(request);
        log.info("Saved request with ID: {}", savedRequest.getId());

        if (dto.getOptions() != null) {
            log.info("Processing {} options", dto.getOptions().size());
            for (RequestDto.OptionDto optionDto : dto.getOptions()) {
                log.info("Adding option: {} = {}", optionDto.getName(), optionDto.getValue());
                RequestOption option = RequestOption.builder()
                        .request(savedRequest)
                        .optionName(optionDto.getName())
                        .selectedValue(optionDto.getValue())
                        .build();
                requestOptionRepository.save(option);
            }
        }

        log.info("Created request with id: {}", savedRequest.getId());
        log.info("=== REQUEST CREATION COMPLETE ===");

        notificationService.notifyStaff(savedRequest);
        webSocketService.notifyNewRequest(savedRequest);

        return savedRequest;
    }

    @Transactional(readOnly = true)
    public List<Request> getAllRequests() {
        log.info("=== GET ALL REQUESTS DEBUG ===");
        List<Request> requests = requestRepository.findAll();
        log.info("Found {} requests in database", requests.size());
        
        for (int i = 0; i < Math.min(requests.size(), 3); i++) {
            Request req = requests.get(i);
            log.info("Request {}: ID={}, Item={}, Status={}, Options={}", 
                i + 1, req.getId(), 
                req.getItem() != null ? req.getItem().getName() : "Unknown", 
                req.getStatus(),
                req.getOptions() != null ? req.getOptions().size() + " options" : "0 options");
            
            if (req.getOptions() != null) {
                req.getOptions().forEach(opt -> 
                    log.info("  - Option: {} = {}", opt.getOptionName(), opt.getSelectedValue()));
            }
        }
        log.info("=== END GET ALL REQUESTS DEBUG ===");
        
        return requests;
    }

    @Transactional
    public Request updateRequestStatus(Long requestId, String status) {
        log.info("Updating request {} status to {}", requestId, status);

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        Request.RequestStatus newStatus;
        try {
            newStatus = Request.RequestStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status + ". Valid values: PENDING, ACCEPTED, COMPLETED");
        }

       if (newStatus == Request.RequestStatus.ACCEPTED || newStatus == Request.RequestStatus.COMPLETED) {

    if (request.getAssignedTo() != null) {
        log.info("Staff {} is updating status to {}", request.getAssignedTo().getName(), newStatus);
    } else {
        log.warn("No staff assigned for request {}", requestId);
    }
}

        request.setStatus(newStatus);

        if (newStatus == Request.RequestStatus.COMPLETED) {
            request.setCompletedAt(LocalDateTime.now());
        }

        Request updatedRequest = requestRepository.save(request);
        log.info("Updated request {} status to {}", requestId, newStatus);

        notificationService.notifyEmployee(updatedRequest);
        webSocketService.sendRequestUpdate(updatedRequest);

        return updatedRequest;
    }

    private User findStaffForFloor(String floor) {
        List<User> staffOnFloor = userService.getStaffByFloor(floor);
        if (!staffOnFloor.isEmpty()) {
            return staffOnFloor.get(0);
        }

        List<User> allStaff = userService.getAllStaff();
        if (!allStaff.isEmpty()) {
            log.info("No staff on floor {}, assigning from available staff", floor);
            return allStaff.get(0);
        }

        log.warn("No staff available in system");
        return null;
    }

   private User findOrCreateUser(RequestDto.UserDto userDto) {

    // Standardize floor format
    String standardizedFloor = standardizeFloorFormat(userDto.getFloor());

    // Try to find existing user by email
    if (userDto.getEmail() != null && !userDto.getEmail().trim().isEmpty()) {
        try {
            User existingUser = userRepository.findByEmail(userDto.getEmail())
                    .orElse(null);

            if (existingUser != null) {
                existingUser.setName(userDto.getName());
                existingUser.setFloor(standardizedFloor);
                return userRepository.save(existingUser);
            }
        } catch (Exception e) {
            log.warn("Error finding user by email: {}", e.getMessage());
        }
    }

    // 🔥 FIX STARTS HERE (CLEAN LOGIC)

    String email = userDto.getEmail();

    if (email == null || email.trim().isEmpty()) {
        email = userDto.getName().toLowerCase().replace(" ", ".")
                + System.currentTimeMillis() + "@company.com";
    }

    User newUser = User.builder()
            .name(userDto.getName())
            .email(email)
            .floor(standardizedFloor)
            .role(User.UserRole.EMPLOYEE)
            .build();

    return userRepository.save(newUser);
}

    private String standardizeFloorFormat(String floor) {
        if (floor == null) return "Ground Floor";
        
        // Convert various formats to standard "Xth Floor" format
        String normalized = floor.trim();
        
        // Handle "Floor 3" -> "3rd Floor"
        if (normalized.matches("Floor \\d+")) {
            int floorNum = Integer.parseInt(normalized.substring(6));
            return getOrdinalFloor(floorNum);
        }
        
        // Handle "Ground" -> "Ground Floor"
        if (normalized.equalsIgnoreCase("Ground")) {
            return "Ground Floor";
        }
        
        // Handle "Nalanda" -> "Nalanda" (keep as is)
        if (normalized.equalsIgnoreCase("Nalanda")) {
            return "Nalanda";
        }
        
        // If already in "Xth Floor" format, return as is
        if (normalized.matches("\\d+(st|nd|rd|th) Floor")) {
            return normalized;
        }
        
        // Default fallback
        return normalized;
    }

    private String getOrdinalFloor(int number) {
        if (number >= 11 && number <= 13) {
            return number + "th Floor";
        }
        switch (number % 10) {
            case 1: return number + "st Floor";
            case 2: return number + "nd Floor";
            case 3: return number + "rd Floor";
            default: return number + "th Floor";
        }
    }
}
