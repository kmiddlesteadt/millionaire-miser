package com.example.ecommerce.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public final class ProductDtos {
    private ProductDtos() {
    }

    public record ProductRequest(
            @NotBlank String name,
            @NotBlank String description,
            @NotNull @DecimalMin("0.01") BigDecimal price,
            @Min(0) int stockQuantity,
            Boolean active
    ) {
    }

    public record ProductResponse(
            Long id,
            String name,
            String description,
            BigDecimal price,
            int stockQuantity,
            boolean active,
            Instant createdAt
    ) {
        static ProductResponse from(Product product) {
            return new ProductResponse(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getStockQuantity(),
                    product.isActive(),
                    product.getCreatedAt()
            );
        }
    }
}
