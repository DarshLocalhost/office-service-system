package com.studioparametric.officeservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.studioparametric.officeservice.entity.Category;
import com.studioparametric.officeservice.entity.Item;
import com.studioparametric.officeservice.entity.ItemOption;
import com.studioparametric.officeservice.entity.User;
import com.studioparametric.officeservice.repository.CategoryRepository;
import com.studioparametric.officeservice.repository.ItemOptionRepository;
import com.studioparametric.officeservice.repository.ItemRepository;
import com.studioparametric.officeservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

   @Bean
CommandLineRunner initData(
        UserRepository userRepository,
        CategoryRepository categoryRepository,
        ItemRepository itemRepository,
        ItemOptionRepository itemOptionRepository,
        PasswordEncoder passwordEncoder // 👈 ADD THIS
) {
        return args -> {
            log.info("Checking if data initialization is needed...");

            // Initialize categories only if they don't exist
            Category beverages = categoryRepository.findByName("Beverages")
                    .orElseGet(() -> {
                        Category newBeverages = Category.builder().name("Beverages").build();
                        categoryRepository.save(newBeverages);
                        log.info("Created Beverages category");
                        return newBeverages;
                    });

            Category snacks = categoryRepository.findByName("Snacks")
                    .orElseGet(() -> {
                        Category newSnacks = Category.builder().name("Snacks").build();
                        categoryRepository.save(newSnacks);
                        log.info("Created Snacks category");
                        return newSnacks;
                    });

            // Initialize users only if they don't exist (check by email)
            if (!userRepository.existsByEmail("john@studio.com")) {
                User employee1 = User.builder()
        .name("John Employee")
        .email("john@studio.com")
        .password(passwordEncoder.encode("1234")) // 🔐 ADD THIS
        .role(User.UserRole.EMPLOYEE)
        .floor("3")
        .build();
                userRepository.save(employee1);
                log.info("Created employee: john@studio.com");
            }

            if (!userRepository.existsByEmail("alice@studio.com")) {
                User staff1 = User.builder()
                        .name("Alice Staff")
                        .email("alice@studio.com")
                        .role(User.UserRole.STAFF)
                        .floor("3")
                        .password(passwordEncoder.encode("1234"))
                        .build();
                userRepository.save(staff1);
                log.info("Created staff: alice@studio.com");
            }

            if (!userRepository.existsByEmail("bob@studio.com")) {
                User staff2 = User.builder()
                        .name("Bob Staff")
                        .email("bob@studio.com")
                        .role(User.UserRole.STAFF)
                        .floor("2")
                        .password(passwordEncoder.encode("1234"))
                        .build();
                userRepository.save(staff2);
                log.info("Created staff: bob@studio.com");
            }

            if (!userRepository.existsByEmail("admin@studio.com")) {
                User admin = User.builder()
                        .name("Admin User")
                        .email("admin@studio.com")
                        .role(User.UserRole.ADMIN)
                        .floor("1")
                        .password(passwordEncoder.encode("1234"))
                        .build();
                userRepository.save(admin);
                log.info("Created admin: admin@studio.com");
            }

            // Initialize items only if they don't exist
            Item coffee = itemRepository.findByName("Coffee")
                    .orElseGet(() -> {
                        Item newCoffee = Item.builder()
                                .name("Coffee")
                                .category(beverages)
                                .isActive(true)
                                .build();
                        itemRepository.save(newCoffee);
                        log.info("Created Coffee item");
                        return newCoffee;
                    });

            Item tea = itemRepository.findByName("Tea")
                    .orElseGet(() -> {
                        Item newTea = Item.builder()
                                .name("Tea")
                                .category(beverages)
                                .isActive(true)
                                .build();
                        itemRepository.save(newTea);
                        log.info("Created Tea item");
                        return newTea;
                    });

            Item milk = itemRepository.findByName("Milk")
                    .orElseGet(() -> {
                        Item newMilk = Item.builder()
                                .name("Milk")
                                .category(beverages)
                                .isActive(true)
                                .build();
                        itemRepository.save(newMilk);
                        log.info("Created Milk item");
                        return newMilk;
                    });

            itemRepository.findByName("Cookies")
                    .orElseGet(() -> {
                        Item newCookies = Item.builder()
                                .name("Cookies")
                                .category(snacks)
                                .isActive(true)
                                .build();
                        itemRepository.save(newCookies);
                        log.info("Created Cookies item");
                        return newCookies;
                    });

            // Initialize item options only if they don't exist
            if (!itemOptionRepository.existsByItemAndName(coffee, "Sugar Level")) {
                ItemOption coffeeSugarLevel = ItemOption.builder()
                        .name("Sugar Level")
                        .type("dropdown")
                        .item(coffee)
                        .build();
                itemOptionRepository.save(coffeeSugarLevel);
                log.info("Created Coffee Sugar Level option");
            }

            if (!itemOptionRepository.existsByItemAndName(coffee, "Milk Type")) {
                ItemOption coffeeMilkType = ItemOption.builder()
                        .name("Milk Type")
                        .type("dropdown")
                        .item(coffee)
                        .build();
                itemOptionRepository.save(coffeeMilkType);
                log.info("Created Coffee Milk Type option");
            }

            if (!itemOptionRepository.existsByItemAndName(tea, "Sugar Level")) {
                ItemOption teaSugarLevel = ItemOption.builder()
                        .name("Sugar Level")
                        .type("dropdown")
                        .item(tea)
                        .build();
                itemOptionRepository.save(teaSugarLevel);
                log.info("Created Tea Sugar Level option");
            }

            if (!itemOptionRepository.existsByItemAndName(tea, "Milk Type")) {
                ItemOption teaMilkType = ItemOption.builder()
                        .name("Milk Type")
                        .type("dropdown")
                        .item(tea)
                        .build();
                itemOptionRepository.save(teaMilkType);
                log.info("Created Tea Milk Type option");
            }

            if (!itemOptionRepository.existsByItemAndName(milk, "Sugar Level")) {
                ItemOption milkSugarLevel = ItemOption.builder()
                        .name("Sugar Level")
                        .type("dropdown")
                        .item(milk)
                        .build();
                itemOptionRepository.save(milkSugarLevel);
                log.info("Created Milk Sugar Level option");
            }

            log.info("Data initialization completed successfully!");
            log.info("Users created: {} employees, {} staff, {} admin",
                    userRepository.findByRole(User.UserRole.EMPLOYEE).size(),
                    userRepository.findByRole(User.UserRole.STAFF).size(),
                    userRepository.findByRole(User.UserRole.ADMIN).size());
        };
    }
}
