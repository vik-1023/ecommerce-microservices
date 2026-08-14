package com.ecommerce.product.service;

import com.ecommerce.product.dto.request.ProductRequest;
import com.ecommerce.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductUsingId(Long id);

    Page<ProductResponse> getAllProducts(
            int page,
            int size,
            String sortBy,
            String direction,
            String name,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    List<ProductResponse> searchProduct(String name);

    List<ProductResponse> filterByCategory(String category);

    List<ProductResponse> getProductByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
}
