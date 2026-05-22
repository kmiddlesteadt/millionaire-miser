package com.example.ecommerce.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class OrderDtos {
    private OrderDtos() {
    }

    public record CreateOrderRequest(
            @NotEmpty List<@Valid OrderLineRequest> items
    ) {
    }

    public record OrderLineRequest(
            @NotNull Long productId,
            @Min(1) int quantity
    ) {
    }

    public record UpdateOrderStatusRequest(
            @NotNull OrderStatus status
    ) {
    }

    public record OrderResponse(
            Long id,
            Long userId,
            String customerEmail,
            List<OrderItemResponse> items,
            BigDecimal total,
            OrderStatus status,
            Instant createdAt
    ) {
        static OrderResponse from(Order order) {
            return new OrderResponse(
                    order.getId(),
                    order.getUser().getId(),
                    order.getUser().getEmail(),
                    order.getItems().stream().map(OrderItemResponse::from).toList(),
                    order.getTotal(),
                    order.getStatus(),
                    order.getCreatedAt()
            );
        }
    }

    public record OrderItemResponse(
            Long productId,
            String productName,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal lineTotal
    ) {
        static OrderItemResponse from(OrderItem item) {
            return new OrderItemResponse(
                    item.getProduct().getId(),
                    item.getProductName(),
                    item.getUnitPrice(),
                    item.getQuantity(),
                    item.getLineTotal()
            );
        }
    }
}
