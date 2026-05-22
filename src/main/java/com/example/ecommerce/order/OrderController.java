package com.example.ecommerce.order;

import static com.example.ecommerce.order.OrderDtos.CreateOrderRequest;
import static com.example.ecommerce.order.OrderDtos.OrderResponse;
import static com.example.ecommerce.order.OrderDtos.UpdateOrderStatusRequest;

import com.example.ecommerce.security.AppUserPrincipal;
import com.example.ecommerce.user.Role;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    OrderResponse create(@AuthenticationPrincipal AppUserPrincipal principal, @Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(principal.getId(), request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    List<OrderResponse> listAll() {
        return orderService.listAll();
    }

    @GetMapping("/me")
    List<OrderResponse> mine(@AuthenticationPrincipal AppUserPrincipal principal) {
        return orderService.listMine(principal.getId());
    }

    @GetMapping("/{id}")
    OrderResponse get(@AuthenticationPrincipal AppUserPrincipal principal, @PathVariable Long id) {
        boolean admin = principal.getUser().getRoles().contains(Role.ADMIN);
        return orderService.getOrder(id, principal.getId(), admin);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateStatus(id, request);
    }
}
