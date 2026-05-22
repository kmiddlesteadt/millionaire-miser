package com.example.ecommerce.product;

import static com.example.ecommerce.product.ProductDtos.ProductRequest;
import static com.example.ecommerce.product.ProductDtos.ProductResponse;

import com.example.ecommerce.common.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> listActive() {
        return productRepository.findByActiveTrueOrderByCreatedAtDesc().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        return ProductResponse.from(findProduct(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product(
                request.name().trim(),
                request.description().trim(),
                request.price(),
                request.stockQuantity()
        );
        product.update(product.getName(), product.getDescription(), product.getPrice(), product.getStockQuantity(), activeOrDefault(request));
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProduct(id);
        product.update(
                request.name().trim(),
                request.description().trim(),
                request.price(),
                request.stockQuantity(),
                activeOrDefault(request)
        );
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = findProduct(id);
        product.update(product.getName(), product.getDescription(), product.getPrice(), product.getStockQuantity(), false);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));
    }

    private boolean activeOrDefault(ProductRequest request) {
        return request.active() == null || request.active();
    }
}
