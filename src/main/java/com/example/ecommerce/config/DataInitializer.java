package com.example.ecommerce.config;

import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.ProductRepository;
import com.example.ecommerce.user.Role;
import com.example.ecommerce.user.User;
import com.example.ecommerce.user.UserRepository;
import java.math.BigDecimal;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            ProductRepository productRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (!userRepository.existsByEmail("admin@example.com")) {
                userRepository.save(new User(
                        "Admin User",
                        "admin@example.com",
                        passwordEncoder.encode("Admin123!"),
                        Set.of(Role.ADMIN, Role.USER)
                ));
            }

            if (!userRepository.existsByEmail("user@example.com")) {
                userRepository.save(new User(
                        "Demo User",
                        "user@example.com",
                        passwordEncoder.encode("User123!"),
                        Set.of(Role.USER)
                ));
            }

            if (productRepository.count() == 0) {
                productRepository.save(new Product(
                        "Everyday Backpack",
                        "Durable commuter backpack with padded laptop storage.",
                        new BigDecimal("79.99"),
                        25
                ));
                productRepository.save(new Product(
                        "Wireless Desk Charger",
                        "Compact 3-in-1 charging stand for phone, watch, and earbuds.",
                        new BigDecimal("49.99"),
                        40
                ));
                productRepository.save(new Product(
                        "Insulated Travel Mug",
                        "Leak-resistant stainless steel mug that keeps drinks hot or cold.",
                        new BigDecimal("24.99"),
                        60
                ));
            }
        };
    }
}
