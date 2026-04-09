package com.studioparametric.officeservice.service;

import com.studioparametric.officeservice.entity.User;
import com.studioparametric.officeservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> getAllStaff() {
        log.info("Fetching all staff users");
        return userRepository.findByRole(User.UserRole.STAFF);
    }

    @Transactional(readOnly = true)
    public List<User> getStaffByFloor(String floor) {
        log.info("Fetching staff on floor: {}", floor);
        return userRepository.findByRoleAndFloor(User.UserRole.STAFF, floor);
    }

    @Transactional(readOnly = true)
    public User getFirstEmployee() {
        log.info("Fetching first employee");
        return userRepository.findByRole(User.UserRole.EMPLOYEE).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No employee found in system"));
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}
