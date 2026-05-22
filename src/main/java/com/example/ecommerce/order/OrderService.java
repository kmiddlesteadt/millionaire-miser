package com.example.ecommerce.order;

import static com.example.ecommerce.order.OrderDtos.CreateOrderRequest;
import static com.example.ecommerce.order.OrderDtos.OrderResponse;
import static com.example.ecommerce.order.OrderDtos.UpdateOrderStatusRequest;

import com.example.ecommerce.common.NotFoundException;
import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.ProductRepository;
import com.example.ecommerce.user.User;
import com.example.ecommerce.user.UserRepository;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
        Order order = new Order(user);

        request.items().forEach(line -> {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + line.productId()));
            if (!product.isActive()) {
                throw new IllegalStateException("Product is not available: " + line.productId());
            }
            product.reserveStock(line.quantity());
            order.addItem(new OrderItem(product, line.quantity()));
        });

        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listAll() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listMine(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId, Long requesterId, boolean admin) {
        Order order = orderRepository.findWithDetailsById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));
        requireOwnerOrAdmin(order, requesterId, admin);
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findWithDetailsById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));
        order.updateStatus(request.status());
        return OrderResponse.from(order);
    }

    private void requireOwnerOrAdmin(Order order, Long requesterId, boolean admin) {
        if (!admin && !order.getUser().getId().equals(requesterId)) {
            throw new AccessDeniedException("You can only access your own orders.");
        }
    }
}
